import pandas as pd
import numpy as np

df = pd.read_json('combined3.json')
df.to_parquet('combined3.parquet.gzip', compression='gzip')