import sys
import calendar

'''python getMonthLastDate.py 2017 12'''
'''Return the last date for a give year-month timestamp'''


def monthLastDate(year, month):
    print(calendar.monthrange(year, month)[1])


if __name__ == '__main__':
    year = int(sys.argv[1])
    month = int(sys.argv[2])
    monthLastDate(year, month)

