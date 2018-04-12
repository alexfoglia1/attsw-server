# ATTSW Project

# Academic Year 2017/18 - Univ. Firenze

Authors: Tommaso Puccetti, Alex Foglia, Francesco Secci

This is a multi-module maven project developed for ATTSW exam. Server handles a database of grids, client retrieve those grids and asks to the server eventually minimal paths in the grids it receives. End2End is a module containing only client-server end to end tests.


In order to automatize integration, we used Travis:

[![Build Status](https://travis-ci.org/alexfoglia1/attsw-server.svg?branch=master)](https://travis-ci.org/alexfoglia1/attsw-server)

To check code coverage, we used Coveralls:

[![Coverage Status](https://coveralls.io/repos/github/alexfoglia1/attsw-server/badge.svg?branch=master)](https://coveralls.io/github/alexfoglia1/attsw-server?branch=master)

To get quality metrics on our code, we used SonarCloud, and here is the link to this project analysis:

[SonarCloud](https://sonarcloud.io/dashboard?id=com.alexfoglia%3Aparent)
