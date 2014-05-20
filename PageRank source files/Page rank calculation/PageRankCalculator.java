import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;


public class PageRankCalculator {
private double RVector[] = new double[Constants._NUMBER_OF_DOCUMENTS];
private int currentIteration = 0;
private ArrayList<RandomAccessFile> columnValuesFiles = new ArrayList<RandomAccessFile>();
private int nextColumnValueFile = 0;
private void InitializeRVector()
{
	//RVector[0] = 1;
	for(int i = 0; i < Constants._NUMBER_OF_DOCUMENTS; i++)
	{
		RVector[i] = 1;		
	}
	
}
private void InitializeColumnValueFiles()
{
	try
	{
		File colValuesLoc = new File(FileNames.columnWiseValueFile);
		File fileNames[] = colValuesLoc.listFiles();
		String sortedNames[] = new String[fileNames.length];
		for(int i = 0; i<fileNames.length; i++)
		{
			sortedNames[i] = fileNames[i].getPath();
		}
		Sort(sortedNames);
		for(int i = 0; i<sortedNames.length; i++)
		{
			RandomAccessFile reader = new RandomAccessFile(sortedNames[i], "r");
			reader.seek(0);
			columnValuesFiles.add(reader);
		}		
	}
	catch(Exception ex)
	{
		Logger.LogMessage("Error at InitializeColumnValueFiles "+ex.getMessage());
	}
}
private void ReloadColumnValueFiles()
{
	try
	{
		for(int i = 0; i<columnValuesFiles.size(); i++)
		{
			RandomAccessFile reader = columnValuesFiles.get(i);
			reader.seek(0);
			
		}
	}
	catch(Exception ex)
	{
		Logger.LogMessage("Error at ReloadColumnValueFiles "+ex.getMessage());
	}
}
private boolean ComputeNextVectorAndCheckConvergence()
{
	boolean hasConverged = false;
	try
	{
	int vectorIndex = 0;
	int columnIndex = 0;
	double nextVector[] = new double[Constants._NUMBER_OF_DOCUMENTS];
	ArrayList<Double> columnValues;
	while(vectorIndex < Constants._NUMBER_OF_DOCUMENTS)
	{
		double sum = 0;
		columnIndex = 0;
		columnValues = this.GetColumnValues();
		
		int i = 0;
		while(columnIndex < columnValues.size())
		{
			sum += RVector[i]* columnValues.get(columnIndex);
			columnIndex++;
			if(columnIndex == columnValues.size() && !ReachedEndOfColumn())
			{
				columnValues = this.GetColumnValues();
				columnIndex = 0;
			}
			i++;
		}
		
		nextVector[vectorIndex] = sum;
		this.InitializeForNextColumn();
		vectorIndex++;
	}
	this.currentIteration++;
	if(currentIteration % 10 == 0)
	{
		double difference = GetDifference(nextVector);
		hasConverged = (difference < (Constants._EPSILON*Constants._NUMBER_OF_DOCUMENTS));
	}
	CopyCurrentVector(nextVector);
	}
	catch(Exception ex)
	{
		Logger.LogMessage("Error at ComputeNextVector "+ex.getMessage());
	}
	return hasConverged;
}
private void CloseFiles()
{
	try
	{
		for(int i = 0; i<this.columnValuesFiles.size(); i++)
		{
			columnValuesFiles.get(i).close();
		}
	}
	catch(Exception ex)
	{
		Logger.LogMessage("Error at CloseFiles "+ex.getMessage());
	}
}
private void CopyCurrentVector(double nextVector[])
{
	for(int i = 0; i<nextVector.length; i++)
	{
		RVector[i] = nextVector[i];
	}
}
private double GetDifference(double nextVector[])
{
	double currentLength, nextLength;
	currentLength = FindL1Length(RVector);
	nextLength = FindL1Length(nextVector);
	return Math.abs(nextLength - currentLength);
}
private double FindL1Length(double vector[])
{
	double length = 0;
	for(int i = 0; i<vector.length; i++)
	{
		length += Math.abs(vector[i]);
	}
	return length;
}
public void ComputePageRank()
{
	this.InitializeRVector();
	this.InitializeColumnValueFiles();
	boolean hasConverged = false;
    Logger.LogMessage("Starting page rank computation");
	while(!hasConverged)
	{
		hasConverged = this.ComputeNextVectorAndCheckConvergence();
		this.ReloadColumnValueFiles();
	}
	WritePageRankToFile();
	CloseFiles();
	Logger.LogMessage("Page rank computation complete");
}
private void WritePageRankToFile()
{
	try
	{
		FileWriter writer = new FileWriter(new File(FileNames.pageRankFile));
		StringBuffer contents = new StringBuffer();
		boolean skipNewLine = true;
		for(int i = 0; i<RVector.length; i++)
		{
			if(skipNewLine)
			{
				skipNewLine = false;
			}
			else
			{
				contents.append("\n");
			}
			contents.append(i+ " "+RVector[i]);
		}
		writer.append(contents);
        writer.close();
	}
	catch(Exception ex)
	{
		Logger.LogMessage("Error at WritePageRankToFile "+ex.getMessage());
	}
}
private ArrayList<Double> GetColumnValues()
{
	try
	{
		ArrayList<Double> values = new ArrayList<Double>();
		
		String line = columnValuesFiles.get(nextColumnValueFile).readLine();
		StringTokenizer tokenizer = new StringTokenizer(line, " ", false);
		tokenizer.nextToken();
		while(tokenizer.hasMoreTokens())
		{
			values.add(Double.parseDouble(tokenizer.nextToken()));
		}
		nextColumnValueFile++;
		return values;
	}
	catch(Exception ex)
	{
		Logger.LogMessage("Error at GetColumnValues "+ex.getMessage());
	}
	return null;
}
private boolean ReachedEndOfColumn()
{
	return nextColumnValueFile == columnValuesFiles.size();
}
private void InitializeForNextColumn()
{
	nextColumnValueFile = 0;
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
public static void main(String args[])
{
	PageRankCalculator pageRankCalculator = new PageRankCalculator();
	pageRankCalculator.ComputePageRank();
	
}
}
