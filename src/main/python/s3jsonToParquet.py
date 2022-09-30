
from operator import truediv
import boto3
import time
import pandas as pd
import numpy as np
"""
Converts JSON files found within an S3 bucket to Parquet format via Pandas
"""
def isQualified(name, prefix, extension) :
    return name.endswith(extension) and name.find(prefix) == 0

def count(bucket, prefix, extension) :

    count = 0
    for file in bucket.objects.all() :
        if (isQualified(file.key, prefix, extension)) :
            count = count + 1
    return count
    
def convertToParquet(file, bucket) :

    fname = file.key
    parquetName = fname[0:-5] + '.parquet'

    path = "s3://" + bucket.name + "/"

    df = pd.read_json(path + fname)
    df.to_parquet(path + parquetName)
    return True

# -----------------------------------------------------

s3 = boto3.resource('s3')

bucket = s3.Bucket('cellsworth-delta-minio')
prefix = "generated/raw"
extension = ".json"

start = time.time()

print("Counting...")

count = count(bucket, prefix, extension)
print( str(count) + " files found" )

print("Converting...")

startCount = count

for file in bucket.objects.all() :
    if (isQualified(file.key, prefix, extension)) :
        if (convertToParquet(file, bucket)) :
            s3.Object(bucket.name, file.key).delete()
            count = count - 1
        print(count)

elapsed = time.time() - start

print()
print(" Time: " + f'{elapsed:.4f}' + ' seconds')
print("Speed: " + f'{elapsed/startCount:.4f}' + ' seconds per file')
