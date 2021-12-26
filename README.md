# Testing in your configuration locally

It's hard to validate your configuration (especially with lots of variable resolution) when you have infrastructure
dependencies. Using the [EnvironmentLoader](src/test/java/com/hpistor/configtestingdemo/helpers/EnvironmentLoader.java)
and [ProfilesTest](src/test/java/com/hpistor/configtestingdemo/ProfileTest.java) allowed us to validate important
configuration settings locally while making changes to the structure of our configuration layout.

## How it works

The [EnvironmentLoader](src/test/java/com/hpistor/configtestingdemo/helpers/EnvironmentLoader.java) loads the
configuration located in your src/main/resources into an Environment and returns it. You can then write tests validating
against this Environment to ensure that your expected profiles and your actual profiles are matching

The tests we have found most useful have been those validating our cassandra keyspaces & kafka topics/consumer-groups.
Our profiles have a lot of variable resolution going on (i.e. regional/environment/dc suffixes) and these tests made it
much easier to verify that our resolution was working as expected before the actual deployment.

I've included two sample junit5 test classes demonstrating some tests that we have found useful. They're written using
junit5's parameterized testing so keep the code as easy to maintain as possible. Simply add a list of profiles you want
activated & create the matching expected configuration file and make changes in your configuration with relative peace
of mind.
