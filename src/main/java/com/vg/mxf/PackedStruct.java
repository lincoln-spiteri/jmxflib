package com.vg.mxf;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vg.util.Struct;

public class PackedStruct extends Struct {

    @Override
    public boolean isPacked() {
        return true;
    }

    public class UTF16String extends Member {

        private final int _length;

        public UTF16String(int lengthBytes) {
            super(lengthBytes << 3, 1);
            _length = lengthBytes; // Takes into account 0 terminator.
        }

        public void set(String string) {
            throw new UnsupportedOperationException();
        }

        public String get() {
            final ByteBuffer buffer = getByteBuffer();
            synchronized (buffer) {
                CharBuffer strb = CharBuffer.allocate(_length / 2);
                int index = getByteBufferPosition() + offset();
                buffer.position(index);
                for (int i = 0; i < _length / 2; i++) {
                    strb.put(buffer.getChar());
                }
                strb.flip();
                return strb.toString();
            }
        }

        public String toString() {
            return this.get();
        }
    }

    Map<String, Object> getStructMembers() {
        try {
            Map<String, Object> members = new LinkedHashMap<String, Object>();
            LinkedList<Class> stack = new LinkedList<Class>();
            stack.push(getClass());
            while (!stack.isEmpty()) {
                Class pop = stack.pop();
                Field[] declaredFields2 = pop.getDeclaredFields();
                for (Field field : declaredFields2) {
                    if (Member.class.isAssignableFrom(field.getType())
                            || Struct.class.isAssignableFrom(field.getType())) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        members.put(field.getName(), field.get(this));
                        field.setAccessible(accessible);
                    }
                }
                Class superclass = pop.getSuperclass();
                if (!Object.class.equals(superclass)) {
                    stack.push(superclass);
                }

            }
            return members;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public String toDebugString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(" [");
        for (Entry<String, Object> entry : getStructMembers().entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
        }
        sb.append("]");
        return sb.toString();
    }

    public Element toXml(Document doc, Element xml) {
        LinkedList<Class> stack = new LinkedList<Class>();
        stack.push(getClass());
        while (!stack.isEmpty()) {
            Class pop = stack.pop();
            Field[] declaredFields2 = pop.getDeclaredFields();
            for (Field field : declaredFields2) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                addAttribute(doc, xml, field);
                field.setAccessible(accessible);
            }
            Class superclass = pop.getSuperclass();
            if (!Object.class.equals(superclass)) {
                stack.push(superclass);
            }
        }

        return xml;
    }

    private void addAttribute(Document doc, Element xml, Field field) {
        try {
            String name = field.getName();
            if (Member.class.isAssignableFrom(field.getType())) {
                Member value = (Member) field.get(this);
                if (value != null) {
                    String stringVal = String.valueOf(value);
                    xml.setAttribute(name, stringVal);
                }
            } else if (PackedStruct.class.isAssignableFrom(field.getType())) {
                PackedStruct value = (PackedStruct) field.get(this);
                if (value != null) {
                    xml.appendChild(value.toXml(doc, doc.createElement(name)));
                }
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    protected ByteBuffer getContent() {
        int pos = getByteBufferPosition();
        ByteBuffer name = getByteBuffer().duplicate();
        name.position(pos + size());
        ByteBuffer slice = name.slice();
        return slice;
    }

    protected ByteBuffer getAllContent() {
        int pos = getByteBufferPosition();
        ByteBuffer name = getByteBuffer().duplicate();
        name.position(pos);
        ByteBuffer slice = name.slice();
        return slice;
    }
}
