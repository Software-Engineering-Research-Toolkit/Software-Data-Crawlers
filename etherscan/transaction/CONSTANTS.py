# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name:       CONSTANTS
   Description:
   Author:     bowen
   date:        6/17/19
-------------------------------------------------
"""
import os

TXS_FNAME = 'eth_txs.csv'
DATA_DIR = '/data/bowen/etherscan-data/data/etherscanData/tx'

TXS_FPATH = os.path.join(DATA_DIR, TXS_FNAME)

COLUMNS = ['tx_hash', 'block', 'timestamp', 'from_address', 'to_address', 'value', 'tx_fee', 'gas_limit',
           'gas_used_by_tx', 'gas_price', 'parsed_input_data', 'raw_input_data', 'contract_address']
