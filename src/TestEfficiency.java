import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Random;
import edu.uwm.cs351.Lexicon;


public class TestEfficiency extends TestCase {
	Lexicon lex;
    private Random random;
    
    private static final int POWER = 21;
    private static final int MAX = 1 << POWER; // 2^20 ~ two million strings
    private static final int BASE_LENGTH = 10000000;
    private static final int TESTS = 500000;
    
	protected void setUp() throws Exception {
		super.setUp();
		random = new Random();

		try {assert 1/0 == 42 : "OK";}
		catch (ArithmeticException ex) {
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);}
		
		lex = new Lexicon();
	}
	
    @Override
    protected void tearDown() {
    	lex = null;
    }
    
    public void buildLargeTree() {
    	for (int power = POWER; power > 0; --power) {
			int incr = 1 << power;
			for (int i= 1 << (power-1); i < MAX; i += incr) {
				lex.add(BASE_LENGTH + i + "");
			}
		}
    }

    public void testAdd() {
		buildLargeTree();
		performOps();
    }
    
    public void testToArray() {
    	buildLargeTree();
    	String[] arr = lex.toArray(null);
    	assertEquals(MAX-1, arr.length);
    	for (int i=BASE_LENGTH + 1; i < BASE_LENGTH + MAX; i++)
    		assertEquals(i+"",arr[i-BASE_LENGTH-1]);
    }
    
    public void testAddAll() {
    	String[] toAdd = new String[MAX];
    	for (int i=1; i < MAX; i++)
    		toAdd[i-1] = BASE_LENGTH + i + "";
    	
		lex.addAll(toAdd, 0, MAX-1);
		performOps();
    }
    
    public void testConsumeAllWithPrefix() {
    	buildLargeTree();
    	ArrayList<String> list = new ArrayList<>();
    	lex.consumeAllWithPrefix(str -> list.add(str), "1");
    	
    	for (int i=BASE_LENGTH + 1; i < BASE_LENGTH + MAX; i++)
    		assertEquals(i+"",list.get(i-BASE_LENGTH-1));
    }
    
    public void testConsumeAllWithPrefix2() {
    	buildLargeTree();
    	ArrayList<String> list = new ArrayList<>();
    	for (int i=1; i < MAX; i++) {
    		String pre = (BASE_LENGTH + i) + "";
    		lex.consumeAllWithPrefix(str -> list.add(str), pre);
    		assertEquals(1,list.size());
    		list.clear();
    	}
    }
    
    private void performOps() {
    	assertEquals(MAX-1, lex.size());
    	assertEquals(BASE_LENGTH + 1 + "", lex.getMin());
    	for (int i=0; i < TESTS; ++i) {
    		int r = BASE_LENGTH + random.nextInt(TESTS);
    		assertEquals(r+1+"",lex.getNext(r+""));
    	}
    }
}
