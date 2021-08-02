from utils import get_formatted_title
import matplotlib.pyplot as plt
import numpy as np
import os

def plot_elapsed(df, title_name, display=False):
    fig = plt.figure()
    title_form = get_formatted_title(title_name)
    plt.plot(range(1, len(df)+1), df.elapsed)
    plt.title(f'Response time {title_form}')
    plt.xlabel('Request #')
    plt.ylabel('Time [ms]')
    fig.set_size_inches(8, 6)
    plt.savefig(f"imgs/tot_reqs_{title_form.replace(' ', '_')}.png", dpi = 200)
    if display:
        plt.show(block = False)
    else:
        plt.close(fig)

def plot_elapsed_by_type(df, title_name, display=False):
    title_form = get_formatted_title(title_name)
    # get # threads
    n_threads = len(df['threadName'].unique())
    x = range(1,n_threads+1) # cycles
    op_per_loop = len(df['threadName'])//n_threads
    labels = df['label'].unique()
    colors = {'GET': 'b', 'POST': 'g', 'PUT': 'k', 'DELETE': 'r'}

    fig, axs = plt.subplots(len(labels))
    fig.suptitle(f'Response time {title_form}')
    for i in range(len(labels)):
        y = df[df['label']==labels[i]].elapsed
        for k in colors.keys():
            if k in labels[i]:
                c = colors[k]
                break 
        axs[i].plot(range(1, len(y)+1), y, "-"+c, label=labels[i])
        #axs[i].set_title(labels[i])
        axs[i].legend(loc='best')
    
    for ax in axs.flat:
        ax.set(xlabel='Request #', ylabel='Time [ms]')
    # Hide x labels and tick labels for top plots and y ticks for right plots.
    for ax in axs.flat:
        ax.label_outer()
    fig.set_size_inches(8, 7)
    plt.savefig(f"imgs/reqs_type_{title_form.replace(' ', '_')}.png", dpi = 300)
    if display:
        plt.show(block = False)
    else:
        plt.close(fig)

def plot_cpu_mem(cpu_file, mem_file, title_name, display=False):
    with open(cpu_file, 'r') as f:
        cpu_data = f.readlines()
    with open(mem_file, 'r') as f:
        mem_data = f.readlines()

    title_form = get_formatted_title(title_name)
    
    # top gives as output sum of loads of all cpus (8 in this case)
    cpu_data = [float(c)/8 for c in cpu_data]
    mem_data = [float(m)/1000 for m in mem_data]

    # time is takes every 0.2s
    x2 = np.arange(0, len(mem_data)/5, 0.2)
    # but top takes more time to compute the output, so scale it to match mem
    # it's not a perfect scaling, but it should be good enough
    end =  len(cpu_data) * (len(x2)//len(cpu_data))
    x = np.mean(x2[:end].reshape(-1, len(x2)//len(cpu_data)), 1)
    fig, axs = plt.subplots(2)
    fig.suptitle(f'Resource usage {title_form}')
    axs[0].plot(x, cpu_data, '-b')
    axs[0].set_title("CPU usage")
    axs[0].set(ylabel='Usage %')
    axs[1].plot(x2[:end], mem_data[:end], '-r')
    axs[1].set_title("RAM usage")
    axs[1].set(xlabel='Time [s]', ylabel='Usage MB') # in KB as per man ps page, divided by 1000 (roughly MB)
    fig.set_size_inches(8, 8)
    plt.savefig(f"imgs/cpu_mem_{title_form.replace(' ', '_')}.png", dpi = 300)
    if display:
        plt.show(block = False)
    else:
        plt.close(fig)


def plot_startup_times(dictt, display=False):
    fig, ax = plt.subplots()
    y = [np.mean(v) for k, v in dictt.items()]
    std = [np.std(v) for k, v in dictt.items()]
    labels = [l.replace(" - ", "\n") for l in dictt.keys()]
    x_pos = np.arange(len(labels))
    ax.bar(x_pos, y, yerr=std, align='center', alpha=0.5, ecolor='black', capsize=10)
    ax.set_ylabel('Startup time [ms]')
    ax.set_xticks(x_pos)
    ax.set_xticklabels(labels)
    ax.tick_params(axis='x', which='major', labelsize=10)
    ax.tick_params(axis='x', which='minor', labelsize=8)
    ax.set_title(r'Startup time of servers ($\pm$ std)')
    ax.yaxis.grid(True)
    fig.set_size_inches(19, 8)
    plt.savefig("imgs/startup_times.png", dpi = 300)
    if display:
        plt.show(block = False)
    else:
        plt.close(fig)