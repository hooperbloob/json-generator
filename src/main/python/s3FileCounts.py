
from operator import truediv
import boto3
import time
import sys, getopt

"""
Counts the number of files in the specified bucket with the chosen extension.
"""
def count(bucketId, extension) :

    print("Counting " + extension + " files in " + bucketId + " ...")

    s3 = boto3.resource('s3')

    bucket = s3.Bucket(bucketId)

    count = 0
    start = time.time()

    for file in bucket.objects.all() :
        if (file.key.endswith(extension)) :
            count = count + 1

    elapsed = time.time() - start

    print()
    print("Count: " + str(count) + " files")
    print(" Time: " + f'{elapsed:.4f}' + ' seconds')


def main(argv):
   
   bucketId = ''
   extension = ''

   try:
      opts, args = getopt.getopt(argv,"hb:e:",["bucket=","extension="])
   except getopt.GetoptError:
      print('s3FileCounts.py -b <bucketid> -e <extension>')
      sys.exit(2)
   for opt, arg in opts:
      if opt == '-h':
         print('s3FileCounts.py -b <bucketid> -e <extension>')
         sys.exit()
      elif opt in ("-b", "--bucket"):
         bucketId = arg
      elif opt in ("-e", "--extension"):
         extension = arg
   
   if (bucketId == '' or extension == '') :
        print('s3FileCounts.py -b <bucketid> -e <extension>')
        sys.exit()

   count(bucketId, extension)

if __name__ == "__main__":
   main(sys.argv[1:])