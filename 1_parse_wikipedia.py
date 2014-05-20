import re
from lxml import etree
import sys

namespace = ['User', 'Talk', 'User','User talk', 'Wikipedia', 'Wikipedia talk', 'File',	'File talk', 'MediaWiki', 'MediaWiki talk', 'Template', 'Template talk','Help',	'Help talk','Category',	'Category talk','Portal', 'Portal talk', 'Book', 'Book talk','Draft', 'Draft talk', 'Education Program', 'Education Program talk','TimedText',	'TimedText talk','Module', 'Module talk']

count = 1
i = 1
entries = []
pattern1 = '\[\[(.*?)\]\]'
pattern2 = '\[\[(.*?)\]\]$'			

f = open('tuple.txt', 'w')

print "Log Info: Started file parse."
for _, element in etree.iterparse(sys.argv[1], tag='{http://www.mediawiki.org/xml/export-0.8/}page'):
	entry = ""
	title = element.findtext('.//{http://www.mediawiki.org/xml/export-0.8/}title')
	ns = element.findtext('.//{http://www.mediawiki.org/xml/export-0.8/}ns')
	if ns == "0":
		body = element.findtext('.//{http://www.mediawiki.org/xml/export-0.8/}text')
		if "#REDIRECT" in body:
			node = re.findall(pattern1, body)[0]
			if node.startswith(":"):
				continue
			if "|" in node:
				node = re.split("[|]", node)[0]
			if "#" in node:
				node = re.split("#", node)[0]
			if ":" in node:
				link = re.split(":", node)[0]
				link = link.lower()
				if any(link == val.lower() for val in namespace):
					continue
			
			entry = entry + node.encode('utf-8') + "/-/\n"
			
		else:
			nodes = re.findall(pattern1, body)
			entry = entry + title.encode('utf-8') + "/-/"
			for node in nodes:
				if node.startswith(":"):
					continue
				if ":" in node:
					links = re.findall(pattern2, node)
					nodes.extend(links)
				else:
					if "|" in node:
						node = re.split("[|]", node)[0]
					if "#" in node:
						node = re.split("#", node)[0]
					if ":" in node:
						link = re.split(":", node)[0]
						link = link.lower()
						if any(link == val.lower() for val in namespace):
							continue	
					entry = entry + node.encode('utf-8') + "//"
					
			entry = entry + "\n"

		entries.append(entry)
		if count % 500000 == 0:
			print "Log Info: Writing to file output" + str(i) + ".txt"
			f = open("output" + str(i) + ".txt", 'w')
			for entry in entries:
				f.write(entry)
			f.close()
			i +=1
			entries = []
		count+=1
	
	element.clear()

print "Log Info: Writing remaining entries to file."

f = open("output" + str(i) + ".txt", 'w')
for entry in entries:
	f.write(entry)

print "Log Info: Completed parsing."		
f.close()
print "Total number of pages : " + str(count)

