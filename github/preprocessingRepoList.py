# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name?     preprocessingRepoList
   Description : 1. remove ?mark time?
                 2. remove duplicate
   Author :       bowenxu
   date?          2/5/18
-------------------------------------------------
   Change Activity:
                   2/5/18:
-------------------------------------------------
"""

from ProgramToolkit.python.lib.utils.file_util import write_file

if __name__ == '__main__':
    orignal_file_path = './2014-fn'
    cases = []
    duplicate_cnt = 0
    with open(orignal_file_path) as file:
        for line in file:
            if line.startswith('mark: ') is False:
                if line.strip() not in cases:
                    cases.append(line.strip())
                else:
                    duplicate_cnt += 1
                    print('DUPLICATE %s' % line.strip())
    print('duplicate_cnt = %s' % duplicate_cnt)
    write_file('2014-repolist', cases)

