from ntpath import join
import matplotlib.pyplot as plt
from utils import *
from plots import *
import os

def main():
    # load csv
    dir = 'raw'
    display = False
    d = load_tests_files(dir)
    for k, v in d.items():
        print(f"Plotting {k}")
        # k = custom name
        # v[0] = is_brute
        # v[1] = full_name
        # v[2] = cpu_name
        # v[3] = mem_name
        filename = os.path.join(dir, v[1])
        df = load_reduced_data(filename)
        if v[0]:
            plot_elapsed(df, k, display)
        else:
            plot_elapsed(df, k, display)
            plot_elapsed_by_type(df, k, display)
        cpu_name = os.path.join(dir, v[2])
        mem_name = os.path.join(dir, v[3])
        plot_cpu_mem(cpu_name, mem_name, k, display)

    # load startup times
    dir = 'startup/'
    l = load_startup_files(dir)
    plot_startup_times(l, display)
    
    if display:
        plt.show()


if __name__ == "__main__": 
    main()
