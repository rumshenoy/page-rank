import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


public class ProbabilityMatrixCreator 
{
	public ProbabilityMatrixCreator()
	{
		
	}
	private HashMap<Integer, ArrayList<Double>>  inLinksMap = new HashMap<Integer, ArrayList<Double>>();
	private int inLinksFileNumber = 1;
	ArrayList<Double> GetInitialRow(ArrayList<Integer> outLinks)
	{
		ArrayList<Double> rowValues = new ArrayList<Double>();
		int i = 0;
		int outLinkInd = 0;
		int numberOfOnes = 0;
		while(outLinkInd < outLinks.size())
		{
			if(i == outLinks.get(outLinkInd))
			{
				numberOfOnes++;
				rowValues.add((double)1);
				i++;
				outLinkInd++;
			}
			else
			{
				rowValues.add((double)0);
				i++;
			}
		}
		while(i<Constants._NUMBER_OF_DOCUMENTS)
		{
			rowValues.add((double)0);
			i++;
		}
		int divideBy = numberOfOnes == 0?Constants._NUMBER_OF_DOCUMENTS:numberOfOnes;
		for(i = 0; i<Constants._NUMBER_OF_DOCUMENTS; i++)
		{
			double value = rowValues.get(i);
			value = value/divideBy;
			value = value * (1-Constants._ALPHA);
			value = value + (Constants._ALPHA/Constants._NUMBER_OF_DOCUMENTS);
			rowValues.set(i, value);
		}
		
		return rowValues;
	}
	void AddValuesToInLinkHashMap(ArrayList<Double> matrixRowValues)
	{
		int numberOfEntries = 0;
		for(int i = 0; i<matrixRowValues.size(); i++)
		{
			ArrayList<Double> currentColumn;
			if(inLinksMap.containsKey(i))
			{
				currentColumn = inLinksMap.get(i);
			}
			else
			{
				currentColumn = new ArrayList<Double>();
			}
			currentColumn.add(matrixRowValues.get(i));
			numberOfEntries = currentColumn.size();
			inLinksMap.put(i, currentColumn);
		}
		int totalSize = (Constants._NUMBER_OF_DOCUMENTS*numberOfEntries)/(1024*1024);
		
		if(totalSize > 25)
		{
			FlushInLinksMapToFile();
		}
	}
	public static void Sort(String sortedNames[])
	{
		for(int i = 1; i<sortedNames.length; i++)
		{
			String key = sortedNames[i];
			int j = i-1;
			for(; j>=0 && key.compareTo(sortedNames[j])<0; j--)
			{
				sortedNames[j+1] = sortedNames[j];
			}
			sortedNames[j+1] = key;
		}
	}
	void FlushInLinksMapToFile()
	{
		try
		{
			StringBuffer contents = new StringBuffer();
			boolean addNewLine = false;
			Integer keys[] = inLinksMap.keySet().toArray(new Integer[0]);
			for(int i = 0; i < keys.length; i++)
			{
				if(addNewLine)
				{
					contents.append("\n");
				}
				else
				{
					addNewLine = true;
				}
				ArrayList<Double> columnValues = inLinksMap.get(keys[i]);
				contents.append(keys[i]+" ");
				for(int j = 0; j < columnValues.size(); j++)
				{
					contents.append(columnValues.get(j)+" ");
				}
			}
			File inLinksFile = new File(FileNames.columnWiseValueFile+inLinksFileNumber);
			if(!inLinksFile.exists())
			{
				inLinksFile.createNewFile();
			}
			FileWriter writer = new FileWriter(inLinksFile);
			writer.write(contents.toString());
			writer.close();
			inLinksFileNumber++;
			inLinksMap = new HashMap<Integer, ArrayList<Double>>();
		}
		catch(Exception ex)
		{
			Logger.LogMessage("Error at FlushLinksMapToFile "+ex.getMessage());
		}
	}
	void ProcessOutLinksFile(String fileName)
	{
		 try
		 {
			 FileReader outLinksFile = new FileReader(fileName);
				BufferedReader reader = new BufferedReader(outLinksFile);
				String currentLine = reader.readLine();
				while(currentLine!= null )
				{	
					StringTokenizer tokenizer = new StringTokenizer(currentLine, " ", false);
					int currentRow;
					currentRow = Integer.parseInt(tokenizer.nextToken());
					ArrayList<Integer> outLinks = new ArrayList<Integer>();
					while(tokenizer.hasMoreTokens())
					{
						outLinks.add(Integer.parseInt(tokenizer.nextToken()));
					}
					
					ArrayList<Double> matrixRowValues = GetInitialRow(outLinks);
					AddValuesToInLinkHashMap(matrixRowValues);
					currentLine = reader.readLine();
				}
				
				reader.close(); 
		 }
		 catch(Exception ex)
		 {
			 Logger.LogMessage("Error in ProcessOutLinksFile "+ex.getMessage());
		 }
	}
	void ProcessOutLinksFiles()
	{
		try
		{
			Logger.LogMessage("Starting matrix creation");
			File outLinksDir = new File(FileNames.outLinksDir);
			File[] files = outLinksDir.listFiles();
			String[] fileNames = new String[files.length];
			for(int i = 0; i<files.length; i++)
			{
				fileNames[i] = files[i].getPath();
			}
			Sort(fileNames);
			for(int i = 0; i<fileNames.length; i++)
			{
				ProcessOutLinksFile(fileNames[i]);
			}
			FlushInLinksMapToFile();
			Logger.LogMessage("Matrix creation complete");
		}
		catch(Exception ex)
		{
			Logger.LogMessage("Error at ProcessOutLinksFile "+ex.getMessage());
		}
	}
	public static void main(String args[])
	{
		
		ProbabilityMatrixCreator matrixCreator = new ProbabilityMatrixCreator();
		matrixCreator.ProcessOutLinksFiles();
	}
}
