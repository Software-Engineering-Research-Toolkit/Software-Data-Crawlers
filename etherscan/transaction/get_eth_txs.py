# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name:       get_eth_txs.py
   Description:
   Author:     bowen
   date:        6/16/19
-------------------------------------------------
"""

import os
import requests
from bs4 import BeautifulSoup
import csv
from crawler.crawl_tx.transaction import TX
from crawler.crawl_tx.CONSTANTS import COLUMNS, DATA_DIR
import time
from ProgrammingToolkit.python.lib.utils.time_util import get_current_time
from path_config import src_dir

MAX_BLK_NUMBER = 8009166
MAX_TX_NUMBER_PER_CON = 1000000


def get_tx_list(url):
    tx_hash_list = list()
    html = requests.get(url).text

    soup = BeautifulSoup(html, features="html.parser")
    for ele in soup.findAll('span', {"class": "hash-tag text-truncate"}):
        if '<a href=\"/tx/' in str(ele):
            tx_hash_list.append(ele.text)
    return tx_hash_list


def get_tx_data(url):
    html = requests.get(url).text

    soup = BeautifulSoup(html, features="html.parser")

    tx = TX()

    for ele in soup.findAll('div', {"class": "row align-items-center mt-1"}):
        if 'Transaction Hash:' in ele.text:
            tx.tx_hash = ele.text.replace('Transaction Hash:', '').strip().replace('\n', '##NEWLINE##')

    for ele in soup.findAll('div', {"class": "row align-items-center"}):
        if 'Block:' in ele.text:
            tx.block = ele.text.replace('Block:', '').strip().replace('\n', '##NEWLINE##').split(' ')[0]
        if 'Timestamp:' in ele.text:
            tx.timestamp = ele.text.replace('Timestamp:', '').strip().replace('\n', '##NEWLINE##')
        if 'From:' in ele.text:
            tx.from_address = ele.text.replace('From:', '').strip().replace('\n', '##NEWLINE##')
        if 'Transaction Fee:' in ele.text:
            tx.tx_fee = ele.text.replace('Transaction Fee:', '').strip().replace('\n', '##NEWLINE##')
        if 'Gas Limit:' in ele.text:
            tx.gas_limit = ele.text.replace('Gas Limit:', '').strip().replace('\n', '##NEWLINE##')
        if 'Gas Used by Transaction:' in ele.text:
            tx.gas_used_by_tx = ele.text.replace('Gas Used by Transaction:', '').strip().replace('\n', '##NEWLINE##')
        if 'Gas Price:' in ele.text:
            tx.gas_price = ele.text.replace('Gas Price:', '').strip().replace('\n', '##NEWLINE##')

    for ele in soup.findAll('div', {"class": "row align-items-center mn-3"}):
        if 'Value:' in ele.text:
            tx.value = ele.text.replace('Value:', '').strip().replace('\n', '##NEWLINE##')

    for ele in soup.findAll('div', {"class": "row"}):
        if 'To:' in ele.text:
            tx.to_address = ele.text.replace('To:', '').strip().replace('\n', '##NEWLINE##')
        if 'Input Data:' in ele.text:
            for input_ele in ele.findAll('textarea', {"id": "inputdata"}):
                tx.parsed_input_data = input_ele.text.strip().replace('\r', '').replace('\n', '##NEWLINE##')
            for row_input_ele in ele.findAll('span', {"id": "rawinput"}):
                tx.raw_input_data = row_input_ele.text.strip()

    if tx.raw_input_data == '':
        tx.raw_input_data = tx.parsed_input_data
        tx.parsed_input_data = None
    return tx


def get_txs(exist_tx_hash_set):
    f = open(txs_fpath, 'a')
    fw = csv.writer(f)
    if not os.path.exists(txs_fpath):
        fw.writerow(COLUMNS)
    for contract_fname in os.listdir(src_dir):
        contract_addr = contract_fname.split('-')[0]
        cnt = 0
        for page in range(1, MAX_TX_NUMBER_PER_CON):
            url = 'https://etherscan.io/txs?a={}&p={}'.format(contract_addr, page)
            tx_hash_list = get_tx_list(url)
            if len(tx_hash_list) == 0:
                break
            print(url, get_current_time())
            for tx_hash in tx_hash_list:
                if (tx_hash, contract_addr) not in exist_tx_hash_set:
                    # debug
                    # tx_hash = '0xcd67088c3506db23277306e6ee5a6ccbef51325ba76478e82d33bf00d9dd4aff'
                    tx_url = 'https://etherscan.io/tx/{}'.format(tx_hash)
                    tx = get_tx_data(tx_url)
                    tx.contract_address = contract_addr
                    time.sleep(3)
                    fw.writerow(tx.as_list())
                    exist_tx_hash_set.add((tx_hash, contract_addr))
                    cnt += 1
                else:
                    print("{} {} already exist!".format(tx_hash, contract_addr))
        print("Wrote {} txs.".format(cnt))
    fw.close()
    f.close()


def get_exist_tx_hash_set(csv_fpath):
    if os.path.exists(csv_fpath):
        tx_hash_set = set()
        with open(csv_fpath, 'r') as rf:
            reader = csv.reader(rf)
            next(reader)
            for row in reader:
                if row[0].strip() != '':
                    tx_hash_set.add((row[0], row[12]))
        return tx_hash_set
    else:
        return set()


txs_fname = 'eth_txs.csv'
txs_fpath = os.path.join(DATA_DIR, txs_fname)

exist_tx_hash_set = get_exist_tx_hash_set(txs_fpath)

get_txs(exist_tx_hash_set)
