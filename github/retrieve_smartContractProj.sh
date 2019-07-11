#!/bin/bash

#run:./retrieve_smartContractProj.sh 12client_secret

clientAccount=$1 
clientCnt=$(cat $clientAccount | wc -l)
pageLimit=100

#successful e.g.,query="https://api.github.com/search/repositories?q=%22smart%20contract%22+in:name,description,readme+created:${startDate}..${endDate}"
#curl -L $query -o 1

################background##########################
###the following 240+882+4895 = 6017 projects contain "smart contract" in repo name, or in description, or in readme file.
#1)240 projects created <=2015-12-31
#2)882 projects created 2016-01-01..2016-12-31
#3)4895 projects created 2017-01-01..2017-12-31


##################start crawling#######################
#GitHub search api only return the maximum 1000 repos, considering the above study results. we decided to take the following way to retrieve all the satisfied repos.
#1) we conducted one search for the year <=2015-12-31, 2016-01-01..2016-12-31, respectively.
#2) for the 2017 year, we seach 12 times, each time search 1 month of 2017.

#e.g., "smart contract" in:name,description,readme created:2012-04-30..2013-04-30
#queryPrefix="https://api.github.com/search/repositories?q=%22smart%20contract%22+in:name,description,readme+created:"

#queryPrefix="https://api.github.com/search/repositories?q=stars%3A>10+forks%3A>10+language%3AJava+created:"
queryPrefix="https://api.github.com/search/repositories?q=stars%3A>9+language%3AJava+created:"
#one_search $startTime $endTime
function one_search(){
    start=$1
    end=$2
    timeRange=$start".."$end

    gotFn=0
    cnt=1
    clientNum=$((cnt%clientCnt + 1))
    client=$(sed -n "${clientNum}p" $clientAccount)
    pageCnt=1
    url=${queryPrefix}"\"${timeRange}\"&page=${pageCnt}&per_page=${pageLimit}&${client}"
    echo $url
    curl -m 120 $url -o tmpFilter

    grep "\"full_name\":" tmpFilter | awk '{print $NF}' | cut -f1 -d "," > tmpFilterFn
    totalCnt=$(grep "\"total_count\":" tmpFilter | awk '{print $NF}' | cut -f1 -d ",")
    echo $timeRange $totalCnt >> totalCnt
    echo "mark:" $timeRange $totalCnt >> fn
    cat tmpFilterFn >>fn
    fnCnt=$(cat tmpFilterFn | wc -l)
    gotFn=$((gotFn+fnCnt))

    rm tmpFilter
    if [ "$totalCnt" = "" ];then
       return 
    fi

    if [ "$gotFn" -eq "$totalCnt" ];then
        return
    fi

    while [ "$gotFn" -lt "$totalCnt" ]
    do
        echo "gotFn $gotFn totalCnt $totalCnt fnCnt $fnCnt"
        pageCnt=$((pageCnt+1))
        cnt=$((cnt+1))
        clientNum=$((cnt%clientCnt + 1))
        client=$(sed -n "${clientNum}p" $clientAccount)
        url=${queryPrefix}"\"${timeRange}\"&page=${pageCnt}&per_page=${pageLimit}&${client}"

        echo $url
        curl -m 120 $url -o tmpFilter

        grep "\"full_name\":" tmpFilter | awk '{print $NF}' | cut -f1 -d "," > tmpFilterFn
        fnCnt=$(cat tmpFilterFn | wc -l)
        gotFn=$((gotFn+fnCnt))
        cat tmpFilterFn >>fn
        rm tmpFilter tmpFilterFn

        if [ "$fnCnt" -eq "0" ]; then
            break
        fi
    done
}

#example run_filter $startYear.
#We will search the projects created within the whole year.
#we divided our search into 12 times, one time for each month, for the sake of getting
#all the projects, as GitHub search will only return 1000 results for each search.
function run_filter(){
    searchYear=$1
    for m in `seq 1 12`
    do
        if [ $m -lt 10 ];then
            mon="0"$m
        else
            mon="$m"
        fi
        firstDate=$searchYear"-"$mon"-""01"
        fiveDate=$searchYear"-"$mon"-""05"
        tenDate=$searchYear"-"$mon"-""10"
        fifteenDate=$searchYear"-"$mon"-""15"
        twentyDate=$searchYear"-"$mon"-""20"
        twentyfiveDate=$searchYear"-"$mon"-""25"
        monLastDate=$(python getMonthLastDate.py $searchYear $m)
        endDate=${searchYear}"-"${mon}"-"${monLastDate}
        # 1-5
        one_search $firstDate $fiveDate
        # 5-10
        one_search $fiveDate $tenDate
        # 10-15
        one_search $tenDate $fifteenDate
        # 15-20
        one_search $fifteenDate $twentyDate
        # 20-25
        one_search $twentyDate $twentyfiveDate
        # 25-last
        one_search $twentyfiveDate $endDate
    done
}

# one_search "2000-01-01" "2015-12-31"
# one_search "2016-01-01" "2016-12-31"
# one_search "2018-01-01" "2018-01-15"
# one_search "2018-01-16" "2018-01-31"
# one_search "2018-02-01" "2018-02-15"
# one_search "2018-02-16" "2018-02-28"
# one_search "2018-03-01" "2018-03-15"
#one_search "2018-03-16" "2018-03-22"
#one_search "2018-03-23" "2018-03-31"
#one_search "2018-04-01" "2018-04-15"
#one_search "2018-04-16" "2018-04-22"
#one_search "2018-04-23" "2018-04-30"
# one_search "2018-05-01" "2018-05-15"
# one_search "2018-05-16" "2018-05-31"
#run_filter 2014
run_filter 2015
#run_filter 2016
#run_filter 2017
#run_filter 2018
