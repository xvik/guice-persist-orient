guice-persist-orient
====================
[![Build Status](https://travis-ci.org/xvik/guice-persist-orient.svg?branch=master)](https://travis-ci.org/xvik/guice-persist-orient)

Guice persist orient db integration

orient introduction https://www.youtube.com/watch?v=o_7NCiTLVis

object db https://github.com/orientechnologies/orientdb/wiki/Object-Database
db url types https://github.com/orientechnologies/orientdb/wiki/Concepts#database_url
multi-threading https://github.com/orientechnologies/orientdb/wiki/Java-Multi-Threading
transactions https://github.com/orientechnologies/orientdb/wiki/Transactions
transactions tuning https://github.com/orientechnologies/orientdb/wiki/Performance-Tuning#wise_use_of_transactions

configuration may be done on instance: db.getStorage().getConfiguration() 
or globally (anywhere in app initialization): OGlobalConfiguration.MVRBTREE_NODE_PAGE_SIZE.setValue(2048);
https://github.com/orientechnologies/orientdb/wiki/Configuration
