# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name:       transaction
   Description:
   Author:     bowen
   date:        6/17/19
-------------------------------------------------
"""

from crawler.crawl_tx.CONSTANTS import COLUMNS


class TX:
    _fields_list = COLUMNS

    tx_hash = None
    block = None
    timestamp = None
    from_address = None
    to_address = None
    value = None
    tx_fee = None
    gas_limit = None
    gas_used_by_tx = None
    gas_price = None
    parsed_input_data = None
    raw_input_data = None
    contract_address = None

    def __init__(self, tx_hash=None, block=None, timestamp=None, from_address=None, to_address=None, value=None,
                 tx_fee=None, gas_limit=None, gas_used_by_tx=None, gas_price=None, parsed_input_data=None,
                 raw_input_data=None, contract_address=None):
        self.tx_hash = tx_hash
        self.block = block
        self.timestamp = timestamp
        self.from_address = from_address
        self.to_address = to_address
        self.value = value
        self.tx_fee = tx_fee
        self.gas_limit = gas_limit
        self.gas_used_by_tx = gas_used_by_tx
        self.gas_price = gas_price
        self.parsed_input_data = parsed_input_data
        self.raw_input_data = raw_input_data
        self.contract_address = contract_address

    def if_complete(self):
        if self.tx_hash is None or self.block is None or self.timestamp is None or self.from_address is None \
                or self.to_address is None or self.value is None or self.tx_fee is None \
                or self.gas_limit is None or self.gas_used_by_tx is None or self.gas_price is None \
                or self.parsed_input_data is None or self.raw_input_data is None or self.contract_address is None:
            return False
        else:
            return True

    def as_list(self):
        return [self.__getattribute__(fieldname) for fieldname in self._fields_list]
