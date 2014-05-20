import sys
import glob

index = {};
matrix = {}
i = 0
j = 1
l =1

for filename in glob.iglob('output*.txt'):
	print "Log Info: Reading " + filename
	f = open(filename, 'r')
	with f as infile:
		for line in infile:
			line = line.strip("\n")
			pair = line.split("/-/", 1)
			key = pair[0]
			if key not in index:
				index[key] = i
				i +=1
			if index[key] not in matrix:
				matrix[index[key]] = []
			values = pair[1].split("//")
			values = filter(None, values)
			for value in values:
				if value not in index:
					index[value] = i
					i+=1
				matrix[index[key]].append(index[value])

	f.close()


print "Log Info: Sorting Index."
sorted_index = [ (v,k) for k,v in index.iteritems() ]
sorted_index.sort() 

print "Log Info: Sorting completed."

print "Log Info: Writing matrices to files."
t = open('matrix' + str(l) + '.txt','w')
print "Log Info: Writing to file matrix" + str(l) + '.txt'
for v,k in sorted_index:
	if j%1000000 == 0:
		t.close()
		l +=1
		print "Log Info: Writing to file matrix" + str(l) + '.txt'
		t = open('matrix' + str(l) + '.txt','w')
	if v in matrix:
		sorted_list = sorted(set(matrix[v]))
		t.write(str(v) + " ")
		t.write(' '.join(str(item) for item in sorted_list))
		t.write('\n')
	else:
		t.write(str(v) + '\n')
	j+=1

print "Number of nodes in index :" + str(j)

t.close()


		
			
			
