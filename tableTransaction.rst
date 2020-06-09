******************
Transactions Table
******************


The transaction table is designed to store each transaction in the database.

* Attributes

  * _id (integer): increased by 1 each insert automatically, primary key

  * fundSymbol (text, not null): unique for each fund, obtained from alphaVantage, should be the same universal

  * fundName (text): also obtained from alphaVantage 

  * date (text, not null): transaction date, mm/dd/yyyy

  * price (real, not null): transaction price

  * shares(integer, not null): transaction share (positive: buy, negative: sell)

  * amount(real, not null): transaction amount


* The order of the records in the transaction table is based on _id. 
  
  * The order of the date is not strictly from the oldest to the newest if the older record is inserted later.




An example of transaction table is shown below:

+-----+------------+-------------------------------+------------+--------+--------+--------+
| _id | fundSymbol |            fundName           |    date    |  price | shares | amount |
+-----+------------+-------------------------------+------------+--------+--------+--------+
|  1  |    BABA    | Alibaba Group Holding Limited | 05/04/2020 | 190.62 |    2   | 381.25 |
+-----+------------+-------------------------------+------------+--------+--------+--------+
|  2  |    BABA    | Alibaba Group Holding Limited | 05/08/2020 | 200.98 |   -1   | 200.98 |
+-----+------------+-------------------------------+------------+--------+--------+--------+
|  3  |    BABA    | Alibaba Group Holding Limited | 05/01/2020 | 196.68 |    2   | 393.37 |
+-----+------------+-------------------------------+------------+--------+--------+--------+
|  4  |     BA     |       The Boeing Company      | 03/30/2020 | 144.24 |    1   | 144.24 |
+-----+------------+-------------------------------+------------+--------+--------+--------+
|  5  |    BABA    | Alibaba Group Holding Limited | 04/28/2020 | 201.78 |    2   | 403.56 |
+-----+------------+-------------------------------+------------+--------+--------+--------+