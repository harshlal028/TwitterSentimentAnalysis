import re
import csv

def cleanup_string(text):
    temp_line = text.encode("utf-8",'replace').decode('utf-8','replace')
    temp_line = re.sub(r"http\S+", "", temp_line)
    temp_line = re.sub(r"@\S+", "", temp_line)
    temp_line = re.sub(r"\n\S+", "", temp_line)
    temp_line = ''.join(e for e in temp_line if e.isalnum() or e.isspace())
    temp_line = temp_line.strip()
    if(temp_line == ''):
        return None
    else:
        return temp_line
    

if __name__ == '__main__':
    print("Function invoked from Main")