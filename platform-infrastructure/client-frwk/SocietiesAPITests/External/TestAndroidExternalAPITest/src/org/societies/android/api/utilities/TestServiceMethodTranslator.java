package org.societies.android.api.utilities;

import org.societies.android.api.utilities.ServiceMethodTranslator;
import org.societies.utilities.DBC.PreconditionException;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

public class TestServiceMethodTranslator extends AndroidTestCase {
	private static final String LOG_TAG = TestServiceMethodTranslator.class.getName();
	
	private static String methodsArray [] = {"getGreeting()", 
												"getGreeting(String appendToMessage)",
												"getAnotherGreeting(String appendToMessage, int index)",
												"getParcelable(String value, int number, org.societies.android.TestParcel parcel)",
												"getArrays(String[ ] value, String othervalue[], int  [] number, long longer  [], org.societies.android.TestParcel parcel[])"
												};
	
	private static String METHOD_1 = "getGreeting";
	private static String METHOD_2 = "getAnotherGreeting";
	private static String METHOD_3 = "getParcelable";
	private static String METHOD_4 = "getArrays";
	private static String METHOD_NON_EXIST = "doesNotExist()";
	private static String PARAM_1_NAME = "appendToMessage";
	private static String PARAM_2_NAME = "index";
	private static String PARAM_3_NAME = "value";
	private static String PARAM_4_NAME = "number";
	private static String PARAM_5_NAME = "parcel";
	private static String PARAM_6_NAME = "othervalue";
	private static String PARAM_7_NAME = "longer";
	
	private static String PARAM_1_TYPE = "String";
	private static String PARAM_2_TYPE = "int";
	private static String PARAM_3_TYPE = "long";
	private static String PARAM_4_TYPE = "double";
	private static String PARAM_5_TYPE = "float";
	private static String PARAM_6_TYPE = "byte";
	private static String PARAM_7_TYPE = "char";
	private static String PARAM_8_TYPE = "boolean";
	private static String PARAM_9_TYPE = "short";
	private static String PARAM_10_TYPE = "org.societies.android.TestParcel";
	
	private static String PARAM_1_ARRAY_TYPE = "String[]";
	private static String PARAM_2_ARRAY_TYPE = "int[]";
	private static String PARAM_3_ARRAY_TYPE = "long[]";
	private static String PARAM_4_ARRAY_TYPE = "double[]";
	private static String PARAM_5_ARRAY_TYPE = "float[]";
	private static String PARAM_6_ARRAY_TYPE = "byte[]";
	private static String PARAM_7_ARRAY_TYPE = "char[]";
	private static String PARAM_8_ARRAY_TYPE = "boolean[]";
	private static String PARAM_9_ARRAY_TYPE = "short[]";
	private static String PARAM_10_ARRAY_TYPE = "org.societies.android.TestParcel[]";
	
	private static String PARAM_1_TYPE_CAP = "String";
	private static String PARAM_2_TYPE_CAP = "Int";
	private static String PARAM_3_TYPE_CAP = "Long";
	private static String PARAM_4_TYPE_CAP = "Double";

	private static String PARAM_1_ARRAY_TYPE_CAP = "String[]";
	private static String PARAM_2_ARRAY_TYPE_CAP = "Int[]";
	private static String PARAM_3_ARRAY_TYPE_CAP = "Long[]";
	private static String PARAM_4_ARRAY_TYPE_CAP = "Double[]";

	protected void setUp() throws Exception {
		super.setUp();
		//after
	}

	protected void tearDown() throws Exception {
		//before
		super.tearDown();
	}

	@MediumTest
	public void testAndroidArrayClassTypes() {
		int integerArray [] = new int [10];
		Log.d(LOG_TAG, "Class integer array: " + integerArray.getClass().getName());
		
		long longArray [] = new long [10];
		Log.d(LOG_TAG, "Class long array: " + longArray.getClass().getName());
		
		double doubleArray [] = new double [10];
		Log.d(LOG_TAG, "Class double array: " + doubleArray.getClass().getName());
		
		float floatArray [] = new float [10];
		Log.d(LOG_TAG, "Class float array: " + floatArray.getClass().getName());
		
		byte byteArray [] = new byte [10];
		Log.d(LOG_TAG, "Class byte array: " + byteArray.getClass().getName());
		
		char charArray [] = new char [10];
		Log.d(LOG_TAG, "Class char array: " + charArray.getClass().getName());
		
		boolean booleanArray [] = new boolean [10];
		Log.d(LOG_TAG, "Class boolean array: " + booleanArray.getClass().getName());
		
		short shortArray [] = new short [10];
		Log.d(LOG_TAG, "Class short array: " + shortArray.getClass().getName());
		
		String stringArray [] = new String [10];
		Log.d(LOG_TAG, "Class String array: " + stringArray.getClass().getName());
		
	}
	@MediumTest
	public void testGetMethodIndex() throws Exception {
		assertEquals(0, ServiceMethodTranslator.getMethodIndex(methodsArray, methodsArray[0]));
		assertEquals(1, ServiceMethodTranslator.getMethodIndex(methodsArray, methodsArray[1]));
		assertEquals(2, ServiceMethodTranslator.getMethodIndex(methodsArray, methodsArray[2]));
		assertEquals(3, ServiceMethodTranslator.getMethodIndex(methodsArray, methodsArray[3]));
		assertEquals(4, ServiceMethodTranslator.getMethodIndex(methodsArray, methodsArray[4]));
		assertEquals(-1, ServiceMethodTranslator.getMethodIndex(methodsArray, METHOD_NON_EXIST));
	}

	@MediumTest
	public void testGetMethodString() throws Exception {
		assertEquals(METHOD_1, ServiceMethodTranslator.getMethodName(methodsArray, 0));
		assertEquals(METHOD_1, ServiceMethodTranslator.getMethodName(methodsArray, 1));
		assertEquals(METHOD_2, ServiceMethodTranslator.getMethodName(methodsArray, 2));
		assertEquals(METHOD_3, ServiceMethodTranslator.getMethodName(methodsArray, 3));
		assertEquals(METHOD_4, ServiceMethodTranslator.getMethodName(methodsArray, 4));
	}
	
	@MediumTest
	public void testGetInvalidMethodString() throws Exception {
		try {
			assertEquals(METHOD_3, ServiceMethodTranslator.getMethodName(methodsArray, 5));
			fail("DBC PreconditionException not thrown");
		} catch (PreconditionException e){
		} 
	}
	
	@MediumTest
	public void testGetParameterNumber() throws Exception {
		assertEquals(0, ServiceMethodTranslator.getParameterNumber(methodsArray[0]));
		assertEquals(1, ServiceMethodTranslator.getParameterNumber(methodsArray[1]));
		assertEquals(2, ServiceMethodTranslator.getParameterNumber(methodsArray[2]));
		assertEquals(3, ServiceMethodTranslator.getParameterNumber(methodsArray[3]));
		assertEquals(5, ServiceMethodTranslator.getParameterNumber(methodsArray[4]));
	}

	
	@MediumTest
	public void testGetMethodParameterName() throws Exception {
		assertEquals(null, ServiceMethodTranslator.getMethodParameterName(methodsArray[0], 0));
		assertEquals(null, ServiceMethodTranslator.getMethodParameterName(methodsArray[0], 1));
		assertEquals(PARAM_1_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[1], 0));
		assertEquals(null, ServiceMethodTranslator.getMethodParameterName(methodsArray[1], 1));
		assertEquals(PARAM_1_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[2], 0));
		assertEquals(PARAM_2_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[2], 1));
		assertEquals(null, ServiceMethodTranslator.getMethodParameterName(methodsArray[2], 2));
		assertEquals(PARAM_3_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[3], 0));
		assertEquals(PARAM_4_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[3], 1));
		assertEquals(PARAM_5_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[3], 2));
		assertEquals(null, ServiceMethodTranslator.getMethodParameterName(methodsArray[3], 3));
		assertEquals(PARAM_3_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[4], 0));
		assertEquals(PARAM_6_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[4], 1));
		assertEquals(PARAM_4_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[4], 2));
		assertEquals(PARAM_7_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[4], 3));
		assertEquals(PARAM_5_NAME, ServiceMethodTranslator.getMethodParameterName(methodsArray[4], 4));

	}

	
	@MediumTest
	public void testGetMethodParameterNames() throws Exception {
		assertEquals(0, ServiceMethodTranslator.getMethodParameterNames(methodsArray[0]).length);
		assertEquals(1, ServiceMethodTranslator.getMethodParameterNames(methodsArray[1]).length);
		assertEquals(2, ServiceMethodTranslator.getMethodParameterNames(methodsArray[2]).length);
		assertEquals(3, ServiceMethodTranslator.getMethodParameterNames(methodsArray[3]).length);
		assertEquals(5, ServiceMethodTranslator.getMethodParameterNames(methodsArray[4]).length);
	}

	@MediumTest
	public void testGetMethodParameterNamesMalFormed() {
		try{
			assertEquals(2, ServiceMethodTranslator.getMethodParameterNames(METHOD_1).length);
			fail("DBC PreconditionException not thrown");
		} catch (PreconditionException e) {
		}
	}

	
	@MediumTest
	public void testGetParameterClasses() throws Exception {
		assertEquals(0, ServiceMethodTranslator.getParameterClasses(methodsArray[0]).length);
		assertEquals(1, ServiceMethodTranslator.getParameterClasses(methodsArray[1]).length);
		assertEquals(2, ServiceMethodTranslator.getParameterClasses(methodsArray[2]).length);
		assertEquals(3, ServiceMethodTranslator.getParameterClasses(methodsArray[3]).length);
		assertEquals(5, ServiceMethodTranslator.getParameterClasses(methodsArray[4]).length);
//		Class clazzes [] = ServiceMethodTranslator.getParameterClasses(methodsArray[2]);
//		for (int i = 0; i < clazzes.length; i++) {
//			System.out.println("Class: " + clazzes[i].getCanonicalName());
//		}
	}
	
	@MediumTest
	public void testCapitaliseString() throws Exception {
		assertEquals(PARAM_1_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_1_TYPE));
		assertEquals(PARAM_2_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_2_TYPE));
		assertEquals(PARAM_3_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_3_TYPE));
		assertEquals(PARAM_4_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_4_TYPE));
		assertEquals(PARAM_10_TYPE, ServiceMethodTranslator.capitaliseString(PARAM_10_TYPE));

		assertEquals(PARAM_1_ARRAY_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_1_ARRAY_TYPE));
		assertEquals(PARAM_2_ARRAY_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_2_ARRAY_TYPE));
		assertEquals(PARAM_3_ARRAY_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_3_ARRAY_TYPE));
		assertEquals(PARAM_4_ARRAY_TYPE_CAP, ServiceMethodTranslator.capitaliseString(PARAM_4_ARRAY_TYPE));
		assertEquals(PARAM_10_ARRAY_TYPE, ServiceMethodTranslator.capitaliseString(PARAM_10_ARRAY_TYPE));
	}
	
	@MediumTest
	public void testGetMethodParameterType() throws Exception {
		assertEquals(PARAM_1_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[1], 0));
		assertEquals(PARAM_1_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[2], 0));
		assertEquals(PARAM_2_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[2], 1));
		assertEquals(PARAM_1_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[3], 0));
		assertEquals(PARAM_2_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[3], 1));
		assertEquals(PARAM_10_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[3], 2));
		assertEquals(PARAM_1_ARRAY_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[4], 0));
		assertEquals(PARAM_1_ARRAY_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[4], 1));
		assertEquals(PARAM_2_ARRAY_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[4], 2));
		assertEquals(PARAM_3_ARRAY_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[4], 3));
		assertEquals(PARAM_10_ARRAY_TYPE, ServiceMethodTranslator.getMethodParameterType(methodsArray[4], 4));

		assertEquals(null, ServiceMethodTranslator.getMethodParameterType(methodsArray[0], 0));
	}
	@MediumTest
	public void testGetMethodParameterTypeCapitalised() throws Exception {
		assertEquals(PARAM_1_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[1], 0));
		assertEquals(PARAM_1_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[2], 0));
		assertEquals(PARAM_2_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[2], 1));
		assertEquals(PARAM_1_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[3], 0));
		assertEquals(PARAM_2_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[3], 1));
		assertEquals(PARAM_10_TYPE, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[3], 2));
		assertEquals(PARAM_1_ARRAY_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[4], 0));
		assertEquals(PARAM_1_ARRAY_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[4], 1));
		assertEquals(PARAM_2_ARRAY_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[4], 2));
		assertEquals(PARAM_3_ARRAY_TYPE_CAP, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[4], 3));
		assertEquals(PARAM_10_ARRAY_TYPE, ServiceMethodTranslator.getMethodParameterTypeCapitalised(methodsArray[4], 4));

	}
	@MediumTest
	public void testGetMethodParameterTypes() throws Exception {
		assertEquals(0, ServiceMethodTranslator.getMethodParameterTypes(methodsArray[0]).length);
		assertEquals(1, ServiceMethodTranslator.getMethodParameterTypes(methodsArray[1]).length);
		assertEquals(2, ServiceMethodTranslator.getMethodParameterTypes(methodsArray[2]).length);
		assertEquals(3, ServiceMethodTranslator.getMethodParameterTypes(methodsArray[3]).length);
		assertEquals(5, ServiceMethodTranslator.getMethodParameterTypes(methodsArray[4]).length);
//		String types [] = ServiceMethodTranslator.getMethodParameterTypes(methodsArray[2]);
//		for (int i = 0; i < types.length; i++) {
//			System.out.println("Types: " + types[i]);
//		}
	}
	
	@MediumTest
	public void testArrayElement() throws Exception {
		assertEquals(0, ServiceMethodTranslator.arrayElement(ServiceMethodTranslator.JAVA_PRIMITIVES, PARAM_2_TYPE));
		assertEquals(1, ServiceMethodTranslator.arrayElement(ServiceMethodTranslator.JAVA_PRIMITIVES, PARAM_3_TYPE));
		assertEquals(2, ServiceMethodTranslator.arrayElement(ServiceMethodTranslator.JAVA_PRIMITIVES, PARAM_4_TYPE));
	}
	
	@MediumTest
	public void testOtherArrayElement() throws Exception {
		assertEquals(0, ServiceMethodTranslator.arrayElement(ServiceMethodTranslator.JAVA_PRIMITIVE_ARRAYS, PARAM_2_ARRAY_TYPE));
		assertEquals(1, ServiceMethodTranslator.arrayElement(ServiceMethodTranslator.JAVA_PRIMITIVE_ARRAYS, PARAM_3_ARRAY_TYPE));
		assertEquals(2, ServiceMethodTranslator.arrayElement(ServiceMethodTranslator.JAVA_PRIMITIVE_ARRAYS, PARAM_4_ARRAY_TYPE));
	}
	
	@MediumTest 
	public void testGetPrimitiveClass() throws Exception {
		assertEquals(int.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_2_TYPE));
		assertEquals(long.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_3_TYPE));
		assertEquals(double.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_4_TYPE));
		assertEquals(float.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_5_TYPE));
		assertEquals(byte.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_6_TYPE));
		assertEquals(char.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_7_TYPE));
		assertEquals(boolean.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_8_TYPE));
		assertEquals(short.class, ServiceMethodTranslator.getPrimitiveClass(PARAM_9_TYPE));
		
		assertEquals(Class.forName(ServiceMethodTranslator.ANDROID_PRIMITIVES_ARRAYS_CLASS_TYPE[0]), ServiceMethodTranslator.getPrimitiveClassArray(PARAM_2_ARRAY_TYPE));
		assertEquals(Class.forName(ServiceMethodTranslator.ANDROID_PRIMITIVES_ARRAYS_CLASS_TYPE[1]), ServiceMethodTranslator.getPrimitiveClassArray(PARAM_3_ARRAY_TYPE));
		assertEquals(Class.forName(ServiceMethodTranslator.ANDROID_PRIMITIVES_ARRAYS_CLASS_TYPE[2]), ServiceMethodTranslator.getPrimitiveClassArray(PARAM_4_ARRAY_TYPE));
		assertEquals(Class.forName(ServiceMethodTranslator.ANDROID_PRIMITIVES_ARRAYS_CLASS_TYPE[3]), ServiceMethodTranslator.getPrimitiveClassArray(PARAM_5_ARRAY_TYPE));
		assertEquals(Class.forName(ServiceMethodTranslator.ANDROID_PRIMITIVES_ARRAYS_CLASS_TYPE[4]), ServiceMethodTranslator.getPrimitiveClassArray(PARAM_6_ARRAY_TYPE));
		assertEquals(Class.forName(ServiceMethodTranslator.ANDROID_PRIMITIVES_ARRAYS_CLASS_TYPE[5]), ServiceMethodTranslator.getPrimitiveClassArray(PARAM_7_ARRAY_TYPE));
		assertEquals(Class.forName(ServiceMethodTranslator.ANDROID_PRIMITIVES_ARRAYS_CLASS_TYPE[6]), ServiceMethodTranslator.getPrimitiveClassArray(PARAM_8_ARRAY_TYPE));
		assertEquals(Class.forName(ServiceMethodTranslator.ANDROID_PRIMITIVES_ARRAYS_CLASS_TYPE[7]), ServiceMethodTranslator.getPrimitiveClassArray(PARAM_9_ARRAY_TYPE));
	}
}
