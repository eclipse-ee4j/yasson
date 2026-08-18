window.BENCHMARK_DATA = {
  "lastUpdate": 1787028941627,
  "repoUrl": "https://github.com/eclipse-ee4j/yasson",
  "entries": {
    "Java JMH Benchmark": [
      {
        "commit": {
          "author": {
            "name": "Kyle Aure",
            "username": "KyleAure",
            "email": "KyleJAure@gmail.com"
          },
          "committer": {
            "name": "GitHub",
            "username": "web-flow",
            "email": "noreply@github.com"
          },
          "id": "432a3b3936a75079dfce4353abb828d2bd56afd3",
          "message": "Merge pull request #743 from eclipse-ee4j/dependabot/maven/junit-jupiter.version-6.1.3\n\nbuild(deps-dev): bump junit-jupiter.version from 6.1.2 to 6.1.3",
          "timestamp": "2026-08-10T13:28:34Z",
          "url": "https://github.com/eclipse-ee4j/yasson/commit/432a3b3936a75079dfce4353abb828d2bd56afd3"
        },
        "date": 1786425306048,
        "tool": "jmh",
        "benches": [
          {
            "name": "org.eclipse.yasson.jmh.CollectionsTest.testDeserialize",
            "value": 26.019115586984007,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 5\nthreads: 1"
          },
          {
            "name": "org.eclipse.yasson.jmh.CollectionsTest.testSerialize",
            "value": 50.274120860994984,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 5\nthreads: 1"
          },
          {
            "name": "org.eclipse.yasson.jmh.ScalarDataTest.testDeserialize",
            "value": 1921.978912452367,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 5\nthreads: 1"
          },
          {
            "name": "org.eclipse.yasson.jmh.ScalarDataTest.testSerialize",
            "value": 3066.9420896043157,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 5\nthreads: 1"
          },
          {
            "name": "org.eclipse.yasson.jmh.TenPropertySerializationTest.testSerialize",
            "value": 863.554779426742,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 5\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "name": "Kyle Aure",
            "username": "KyleAure",
            "email": "KyleJAure@gmail.com"
          },
          "committer": {
            "name": "GitHub",
            "username": "web-flow",
            "email": "noreply@github.com"
          },
          "id": "36eb036acbe2955af1fb54af584c6575c352c011",
          "message": "Merge pull request #671 from lvydra/issue655\n\n[655] java.sql.Time throws java.lang.UnsupportedOperationException when serialized",
          "timestamp": "2026-08-12T16:29:45Z",
          "url": "https://github.com/eclipse-ee4j/yasson/commit/36eb036acbe2955af1fb54af584c6575c352c011"
        },
        "date": 1787028941105,
        "tool": "jmh",
        "benches": [
          {
            "name": "org.eclipse.yasson.jmh.CollectionsTest.testDeserialize",
            "value": 24.104032222448218,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 5\nthreads: 1"
          },
          {
            "name": "org.eclipse.yasson.jmh.CollectionsTest.testSerialize",
            "value": 49.95231094406047,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 5\nthreads: 1"
          },
          {
            "name": "org.eclipse.yasson.jmh.ScalarDataTest.testDeserialize",
            "value": 2005.0443713489542,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 5\nthreads: 1"
          },
          {
            "name": "org.eclipse.yasson.jmh.ScalarDataTest.testSerialize",
            "value": 2907.241007787411,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 5\nthreads: 1"
          },
          {
            "name": "org.eclipse.yasson.jmh.TenPropertySerializationTest.testSerialize",
            "value": 873.0292100833034,
            "unit": "ops/ms",
            "extra": "iterations: 5\nforks: 5\nthreads: 1"
          }
        ]
      }
    ]
  }
}