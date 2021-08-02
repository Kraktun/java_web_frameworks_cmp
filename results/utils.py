import pandas as pd
from datetime import datetime
from pathlib import Path


def load_data(file):
    cols = ['timeStamp', 'elapsed', 'label', 'responseCode', 'threadName', 'success', 'bytes', 'sentBytes', 'grpThreads', 'Latency', 'IdleTime', 'Connect']
    df = pd.read_csv(file, usecols=cols)
    # sort by thread number:
    # get # of http requests per loop
    reqs = df['label'].replace(r'^[0-9]+-[0-9]+$', '').unique()
    num_reqs = len(reqs)
    # extract numbers
    df['threadNum'] = df.threadName.replace({r'.*([0-9]+)-([0-9]+)': r'\1'+ ' ' +r'\2' }, regex=True)
    # convert to int in ascending order
    df.threadNum = [int(i.split(' ')[0])*num_reqs + int(i.split(' ')[1]) for i in df.threadNum]
    df.sort_values(by=['timeStamp'], inplace = True)
    return df

def load_reduced_data(file):
    cols = ['timeStamp', 'elapsed', 'label', 'threadName', 'success', 'Latency']
    df = pd.read_csv(file, usecols=cols)
    # sort by thread number:
    # get # of http requests per loop
    reqs = df['label'].replace(r'^[0-9]+-[0-9]+$', '').unique()
    num_reqs = len(reqs)
    # extract numbers
    df['threadNum'] = df.threadName.replace({r'.*([0-9]+)-([0-9]+)': r'\1'+ ' ' +r'\2' }, regex=True)
    # convert to int in ascending order
    df.threadNum = [int(i.split(' ')[0])*num_reqs + int(i.split(' ')[1]) for i in df.threadNum]
    df.sort_values(by=['timeStamp'], inplace = True)
    return df

def get_times(file):
    times = []
    with open(file, 'r') as f:
        lines = f.readlines()
        format = '%H:%M:%S.%f'
        for r in range(0, len(lines), 2):
            start_time = lines[r]
            start_time = start_time[len("Start: "):].strip()
            start_time = datetime.strptime(start_time, format)
            end_time = lines[r+1]
            end_time = end_time[len("End: "):].strip()
            end_time = datetime.strptime(end_time, format)
            times.append((end_time-start_time).total_seconds() * 1000)
    return times

# beware: hardcoded values
def load_startup_files(dir):
    p = Path(dir)
    files = p.glob('**/*.txt')
    labs = {}
    for f in files:
        name = f.stem.split('_startup')[0]
        java = f.parent.stem
        if java == 'startup':
            java = 'native'
            name = name.split('_native')[0]
        name = f"{name} - {java}"
        labs[name] = get_times(f.absolute())
    return labs

# beware: hardcoded values
def load_tests_files(dir):
    p = Path(dir)
    files = p.glob('**/*.csv')
    # each csv should be accompanied by two txt
    labs = {}
    for f in files:
        is_brute = False
        name_f = f.stem.split('_CRUD')[0]
        if name_f == f.stem:
            # it's a brute test
            is_brute = True
        name_f = f.stem
        cpu_name = name_f + '_cpu.txt'
        rss_name = name_f + '_rss.txt'
        full_name = f.name
        java = f.parent.stem
        if java == 'results':
            java = 'native'
            #name_f = name_f.split('_native')[0]
        else:
            cpu_name = java + "/" + cpu_name
            rss_name = java + "/" + rss_name
            full_name = java + "/" + full_name
        name_f = f"{name_f} - {java}"
        labs[name_f] = [is_brute, full_name, cpu_name, rss_name]
    return labs

def get_formatted_title(title_name):
    # first remove _native
    title_form= title_name.replace('_native', '')
    title_form = title_form.split(" - ")
    title_form = title_form[0] + "[" + title_form[1] + "]"
    title_form_t = title_form.split("_CRUD")
    if len(title_form_t) > 1:
        # crud
        title_form = title_form_t[0] + " [CRUD][" + title_form_t[1].split("[")[1]
    else:
        # brute
        title_form_t = title_form.split("_rsa_brute")
        title_form = title_form_t[0] + " [BRUTE][" + title_form_t[1].split("[")[1]
        
    return title_form
