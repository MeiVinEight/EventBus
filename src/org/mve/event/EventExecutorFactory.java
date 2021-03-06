package org.mve.event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class EventExecutorFactory
{
	private static final String HEX = "0123456789ABCDEF";
	private static final ClassLoader LOADER = EventExecutorFactory.class.getClassLoader();
	private static int index = 0;
	private static Method define = null;

	static
	{
		try
		{
			define = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
			define.setAccessible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static EventExecutor create(Listener listener, Method method) throws IllegalAccessException
	{
		if (define != null)
		{
			try
			{
				String s;
				String hash = hex32(index++);
				String name = "org.mve.event.core.SimpleEventExecutor&"+hash;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int[] i = new int[]
				{
					0xCA, 0xFE, 0xBA, 0xBE, 0x00, 0x00, 0x00, 0x34,  0x00, 0x2A, 0x0A, 0x00, 0x0B, 0x00, 0x1B, 0x07,
					0x00, 0x1C, 0x07, 0x00, 0x1D, 0x07, 0x00, 0x1E,  0x0A, 0x00, 0x03, 0x00, 0x1F, 0x07, 0x00, 0x20,
					0x07, 0x00, 0x21, 0x08, 0x00, 0x22, 0x0A, 0x00,  0x07, 0x00, 0x23, 0x07, 0x00, 0x24, 0x07, 0x00,
					0x25, 0x07, 0x00, 0x26, 0x01, 0x00, 0x06, 0x3C,  0x69, 0x6E, 0x69, 0x74, 0x3E, 0x01, 0x00, 0x03,
					0x28, 0x29, 0x56, 0x01, 0x00, 0x04, 0x43, 0x6F,  0x64, 0x65, 0x01, 0x00, 0x0F, 0x4C, 0x69, 0x6E,
					0x65, 0x4E, 0x75, 0x6D, 0x62, 0x65, 0x72, 0x54,  0x61, 0x62, 0x6C, 0x65, 0x01, 0x00, 0x07, 0x65,
					0x78, 0x65, 0x63, 0x75, 0x74, 0x65, 0x01, 0x00,  0x30, 0x28, 0x4C, 0x6F, 0x72, 0x67, 0x2F, 0x6D,
					0x76, 0x65, 0x2F, 0x65, 0x76, 0x65, 0x6E, 0x74,  0x2F, 0x4C, 0x69, 0x73, 0x74, 0x65, 0x6E, 0x65,
					0x72, 0x3B, 0x4C, 0x6F, 0x72, 0x67, 0x2F, 0x6D,  0x76, 0x65, 0x2F, 0x65, 0x76, 0x65, 0x6E, 0x74,
					0x2F, 0x45, 0x76, 0x65, 0x6E, 0x74, 0x3B, 0x29,  0x56, 0x01, 0x00, 0x0D, 0x53, 0x74, 0x61, 0x63,
					0x6B, 0x4D, 0x61, 0x70, 0x54, 0x61, 0x62, 0x6C,  0x65, 0x07, 0x00, 0x1E, 0x07, 0x00, 0x1C, 0x07,
					0x00, 0x1D, 0x07, 0x00, 0x20, 0x01, 0x00, 0x0A,  0x45, 0x78, 0x63, 0x65, 0x70, 0x74, 0x69, 0x6F,
					0x6E, 0x73, 0x01, 0x00, 0x0A, 0x53, 0x6F, 0x75,  0x72, 0x63, 0x65, 0x46, 0x69, 0x6C, 0x65, 0x01,
					0x00, 0x18, 0x53, 0x69, 0x6D, 0x70, 0x6C, 0x65,  0x45, 0x76, 0x65, 0x6E, 0x74, 0x45, 0x78, 0x65,
					0x63, 0x75, 0x74, 0x6F, 0x72, 0x2E, 0x6A, 0x61,  0x76, 0x61, 0x0C, 0x00, 0x0D, 0x00, 0x0E, 0x01
				};
				out.write(intToByte(i), 0, i.length);
				s = method.getParameterTypes()[0].getName().replace('.', '/');
				writeUTF(s, out);
				out.write(new byte[]{0x01}, 0, 1);
				s = listener.getClass().getName().replace('.', '/');
				writeUTF(s, out);
				i = new int[]
				{
					                                          0x01,  0x00, 0x1C, 0x6A, 0x61, 0x76, 0x61, 0x2F, 0x6C,
					0x61, 0x6E, 0x67, 0x2F, 0x43, 0x6C, 0x61, 0x73,  0x73, 0x43, 0x61, 0x73, 0x74, 0x45, 0x78, 0x63,
					0x65, 0x70, 0x74, 0x69, 0x6F, 0x6E, 0x0C, 0x00,  0x27, 0x00, 0x28, 0x01, 0x00, 0x13, 0x6A, 0x61,
					0x76, 0x61, 0x2F, 0x6C, 0x61, 0x6E, 0x67, 0x2F,  0x45, 0x78, 0x63, 0x65, 0x70, 0x74, 0x69, 0x6F,
					0x6E, 0x01, 0x00, 0x1C, 0x6F, 0x72, 0x67, 0x2F,  0x6D, 0x76, 0x65, 0x2F, 0x65, 0x76, 0x65, 0x6E,
					0x74, 0x2F, 0x45, 0x76, 0x65, 0x6E, 0x74, 0x45,  0x78, 0x63, 0x65, 0x70, 0x74, 0x69, 0x6F, 0x6E,
					0x01, 0x00, 0x13, 0x43, 0x61, 0x6E, 0x20, 0x6E,  0x6F, 0x74, 0x20, 0x70, 0x61, 0x73, 0x73, 0x20,
					0x65, 0x76, 0x65, 0x6E, 0x74, 0x20, 0x0C, 0x00,  0x0D, 0x00, 0x29, 0x01
				};
				out.write(intToByte(i), 0, i.length);
				s = name.replace('.', '/');
				writeUTF(s, out);
				i = new int[]
				{
					                        0x01, 0x00, 0x10, 0x6A,  0x61, 0x76, 0x61, 0x2F, 0x6C, 0x61, 0x6E, 0x67,
					0x2F, 0x4F, 0x62, 0x6A, 0x65, 0x63, 0x74, 0x01,  0x00, 0x20, 0x6F, 0x72, 0x67, 0x2F, 0x6D, 0x76,
					0x65, 0x2F, 0x65, 0x76, 0x65, 0x6E, 0x74, 0x2F,  0x63, 0x6F, 0x72, 0x65, 0x2F, 0x45, 0x76, 0x65,
					0x6E, 0x74, 0x45, 0x78, 0x65, 0x63, 0x75, 0x74,  0x6F, 0x72, 0x01
				};
				out.write(intToByte(i), 0, i.length);
				s = method.getName();
				writeUTF(s, out);
				out.write(new byte[]{0x01}, 0, 1);
				s = "(L"+method.getParameterTypes()[0].getName().replace('.', '/')+";)V";
				writeUTF(s, out);
				i = new int[]
				{
					                        0x01, 0x00, 0x2A, 0x28,  0x4C, 0x6A, 0x61, 0x76, 0x61, 0x2F, 0x6C, 0x61,
					0x6E, 0x67, 0x2F, 0x53, 0x74, 0x72, 0x69, 0x6E,  0x67, 0x3B, 0x4C, 0x6A, 0x61, 0x76, 0x61, 0x2F,
					0x6C, 0x61, 0x6E, 0x67, 0x2F, 0x54, 0x68, 0x72,  0x6F, 0x77, 0x61, 0x62, 0x6C, 0x65, 0x3B, 0x29,
					0x56, 0x00, 0x21, 0x00, 0x0A, 0x00, 0x0B, 0x00,  0x01, 0x00, 0x0C, 0x00, 0x00, 0x00, 0x02, 0x00,
					0x01, 0x00, 0x0D, 0x00, 0x0E, 0x00, 0x01, 0x00,  0x0F, 0x00, 0x00, 0x00, 0x1D, 0x00, 0x01, 0x00,
					0x01, 0x00, 0x00, 0x00, 0x05, 0x2A, 0xB7, 0x00,  0x01, 0xB1, 0x00, 0x00, 0x00, 0x01, 0x00, 0x10,
					0x00, 0x00, 0x00, 0x06, 0x00, 0x01, 0x00, 0x00,  0x00, 0x09, 0x00, 0x01, 0x00, 0x11, 0x00, 0x12,
					0x00, 0x02, 0x00, 0x0F, 0x00, 0x00, 0x00, 0x8F,  0x00, 0x04, 0x00, 0x06, 0x00, 0x00, 0x00, 0x29,
					0x2C, 0xC0, 0x00, 0x02, 0x4E, 0x2B, 0xC0, 0x00,  0x03, 0x3A, 0x04, 0xA7, 0x00, 0x06, 0x3A, 0x05,
					0xB1, 0x19, 0x04, 0x2D, 0xB6, 0x00, 0x05, 0xA7,  0x00, 0x11, 0x3A, 0x05, 0xBB, 0x00, 0x07, 0x59,
					0x12, 0x08, 0x19, 0x05, 0xB7, 0x00, 0x09, 0xBF,  0xB1, 0x00, 0x02, 0x00, 0x00, 0x00, 0x0B, 0x00,
					0x0E, 0x00, 0x04, 0x00, 0x11, 0x00, 0x17, 0x00,  0x1A, 0x00, 0x06, 0x00, 0x02, 0x00, 0x10, 0x00,
					0x00, 0x00, 0x2A, 0x00, 0x0A, 0x00, 0x00, 0x00,  0x12, 0x00, 0x05, 0x00, 0x13, 0x00, 0x0B, 0x00,
					0x18, 0x00, 0x0E, 0x00, 0x15, 0x00, 0x10, 0x00,  0x17, 0x00, 0x11, 0x00, 0x1B, 0x00, 0x17, 0x00,
					0x20, 0x00, 0x1A, 0x00, 0x1D, 0x00, 0x1C, 0x00,  0x1F, 0x00, 0x28, 0x00, 0x21, 0x00, 0x13, 0x00,
					0x00, 0x00, 0x14, 0x00, 0x04, 0x4E, 0x07, 0x00,  0x14, 0xFD, 0x00, 0x02, 0x07, 0x00, 0x15, 0x07,
					0x00, 0x16, 0x48, 0x07, 0x00, 0x17, 0x0D, 0x00,  0x18, 0x00, 0x00, 0x00, 0x04, 0x00, 0x01, 0x00,
					0x07, 0x00, 0x01, 0x00, 0x19, 0x00, 0x00, 0x00,  0x02, 0x00, 0x1A
				};
				out.write(intToByte(i), 0, i.length);
				out.flush();
				out.close();
				byte[] code = out.toByteArray();
				Class<?> c = (Class<?>)define.invoke(LOADER, name, code, 0, code.length);
				return (EventExecutor) c.newInstance();
			}
			catch (Exception e)
			{
				System.out.println("Can not create event executor from EventExecutorFactory, caused by an exception");
				e.printStackTrace();
			}
		}
		return new SimpleReflectionEventExecutor(method.getParameterTypes()[0].asSubclass(Event.class), method);
	}

	private static byte[] byteArray16(short num)
	{
		byte[] b = new byte[2];
		b[0] = (byte)(num >>> 8);
		b[1] = (byte)num;
		return b;
	}

	private static String hex32(int num)
	{
		char[] ch = new char[8];
		for (int i=0; i<8; i++)
		{
			int c = 7 - i;
			int b = num >>> (i * 4);
			ch[c] = hexBinary(b);
		}
		return new String(ch);
	}

	private static char hexBinary(int i)
	{
		return HEX.charAt((i & 2147483647) % 16);
	}

	private static byte[] intToByte(int[] arr)
	{
		byte[] b = new byte[arr.length];
		for (int i = 0; i < arr.length; i++)
		{
			b[i] = (byte) arr[i];
		}
		return b;
	}

	private static void writeUTF(String str, OutputStream out) throws IOException
	{
		byte[] b = str.getBytes(StandardCharsets.UTF_8);
		byte[] t = byteArray16((short)b.length);
		out.write(t, 0, t.length);
		out.write(b, 0, b.length);
	}
}
