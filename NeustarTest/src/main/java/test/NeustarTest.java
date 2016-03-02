package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//------------------------------------------------------------------------
//Neustar: Accepts a file as its first argument and reads through each line. Each line is
//processed with the first word element without spaces being a category and the second which
//accepts spaces is the subcategory. Each pair of category and subcategory is tracked and
//the count kept for each unique pair for a category. Outputs a list of the counts of the 
//number of unique pairs per category. It also outputs all unique pairs.
//-Data is stored in 3 lists
//------------------------------------------------------------------------

public class NeustarTest {
	//----------------------data types------------------------------------
	
	public enum ValidCategory{
		PERSON("PERSON"),
		PLACE("PLACE"),
		ANIMAL("ANIMAL"),
		COMPUTER("COMPUTER"),
		OTHER("OTHER");
		
		private final String name;
		
		private ValidCategory(String name) {
			this.name = name;
		}
		
		public String asString() {
			return this.name;
		}
		
		public static ValidCategory getValue(String name) {
			
			for(ValidCategory category : ValidCategory.values()) {
				if (category.name.contentEquals(name)) {
					return category;
				}
			}
			
			return null;
		}
	}
	
	private List<Entry<ValidCategory, String>> categoryPairsFound = new ArrayList<Entry<ValidCategory, String>>();
	
	private int nextCategoryOrderIndex = 1;												//a counter to keep track of the order categories are encountered
	
	private List<Entry<ValidCategory, Integer>> categoryOrder = new ArrayList<Entry<ValidCategory, Integer>>();			//each category has the order in which it is found stored here

	private List<Entry<ValidCategory, Integer>> categoryCount = new ArrayList<Entry<ValidCategory, Integer>>();
	
	//--------------------------------------------------------------------
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NeustarTest neustarTest = new NeustarTest();
		
		neustarTest.main(args);							//bootstrap the true main function
	}
	
	public void nMain(String[] args) {
		String inputFileName = null;
//		File inputFile = null;
		BufferedReader inputFileReader = null;
		
		//get file
		inputFileName = args[0];
		if ( inputFileName != null) {
			try {

				inputFileReader = new BufferedReader(new FileReader(inputFileName));
			}
			catch(Exception e) {
				System.out.println("File Not Found.");
				System.exit(1);
			}
		}
		
		//load category  pairs from file
		if (inputFileReader != null) {
			AddAllCategoryPairsFromFile(inputFileReader);
		}
		
	}

	//--------------------------------------------------------------------
	
	private boolean categoryHasBeenFound(ValidCategory validCategory) {
		for(Entry entry: categoryOrder) {
			if (entry.getKey() == validCategory)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean categoryPairHasBeenFound (ValidCategory validCategory, String subCategory) {
		for(Entry entry : categoryPairsFound) {
			if (entry.getKey() == validCategory) {
				if (entry.getValue() ==  subCategory) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void incrementCategoryCount(ValidCategory validCategory) {
		//increment category count if it exists
		for(Entry entry : categoryCount) {
			if (entry.getKey() ==  validCategory) {
				int currentCount = 0;
				if (entry.getValue() != null) {
					currentCount = ((Integer)entry.getValue()).intValue();
				}
				entry.setValue(currentCount + 1);
				return;
			}
		}
		
		//category count does not exist, add it
		categoryCount.add(new AbstractMap.SimpleEntry<ValidCategory, Integer>(validCategory, 1));
	}

	//getSubCategory: takes a whole line and get the second element all the way till the end of line
	//including spaces and other unique characters.
	private String getSubCategory(String line) {
		String regexStr = "^\\w+ (.+)$";
		
		Pattern pattern = Pattern.compile(regexStr);
		Matcher matcher =  pattern.matcher(line);
		
		if (matcher.find()) {
			return matcher.group(1);
		}
		
		return null;
	}

	//getCategory: takes a whole line and gets the first element of all word characters, no spaces
	private String getCategory(String line) {
		String regexStr = "^(\\w+) .+$";
		
		Pattern pattern = Pattern.compile(regexStr);
		Matcher matcher =  pattern.matcher(line);
		
		if (matcher.find()) {
			return matcher.group(1);
		}
		
		return null;
	}

	//addCategoryPairFromLine: accepts a whole line and processes it, getting a category subcategory pair
	//if unique the pair is stored and the counter for that category is increased. If a category does not
	//yet exist it is added to the order and counter lists.
	private void addCategoryPairFromLine(String line) {
		String categoryName = null;
		ValidCategory category = null;
		String subCategory = null;

		//get category from line
		categoryName = getCategory(line);
		if ((categoryName != null) && (!categoryName.isEmpty())) {														//cannot be null
			category = ValidCategory.getValue(categoryName);
			if (category != null) {
				//get subcategory
				subCategory = getSubCategory(line);
				if ((subCategory != null) && (!subCategory.isEmpty())) {
					if (!categoryPairHasBeenFound(category, subCategory)) {
						//create category pair
						categoryPairsFound.add(new AbstractMap.SimpleEntry<ValidCategory, String>(category, subCategory));
						incrementCategoryCount(category);
					}

					//if this is a new category found keep track of the index
					if (!categoryHasBeenFound(category)) {
						categoryOrder.add(new AbstractMap.SimpleEntry<ValidCategory, Integer>(category, nextCategoryOrderIndex));
						nextCategoryOrderIndex++;
					}
				}
			}
		}
	}

	//addAllCategoryPairsFromFile:accpets the file and loops through all lines and calls them to be processed
	private void AddAllCategoryPairsFromFile(BufferedReader inputFileReader) {
		String line =  null;
		
		try {
			while((line = inputFileReader.readLine()) !=  null) {
				addCategoryPairFromLine(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		printCategoryResults();
	}

	//printCategoryResults: Print a header then all categories in order of discovery allow with the count of unique pairs found
	//finally print all unique pairs found.
	private void printCategoryResults() {
		int currentCategoryOrderIndex = 1;
		int categoryIndex = 0;
		ValidCategory category;
		Integer count;
		
		//print all category counts
		System.out.println("CATEGORY     COUNT");
		for (currentCategoryOrderIndex = 1 ;  (currentCategoryOrderIndex <= categoryCount.size()) ;  currentCategoryOrderIndex++) {
			//find next category in order
			for(categoryIndex = 0 ; (categoryOrder.get(categoryIndex).getValue() != currentCategoryOrderIndex) ; categoryIndex++) {
			}
			category = categoryOrder.get(categoryIndex).getKey();
			
			//output current category and count
			System.out.print(category.asString() + "     ");
			for(categoryIndex = 0 ; (categoryCount.get(categoryIndex).getKey() != category) ; categoryIndex++) {
			}
			count = categoryCount.get(categoryIndex).getValue();
			if (count != null) {
				System.out.println(count.intValue());
			}
			else {																					//this should not be possible
				System.out.print(0);
			}
		}
		
		System.out.println("");
		
		//print all category pairs
		for(Entry entry : categoryPairsFound) {
			System.out.printf("%s %s\n", entry.getKey().toString(), entry.getValue());
		}
	}
	
	

	//--------------------------------------------------------------------
}
