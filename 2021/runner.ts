import _ from 'lodash'

require('child_process').fork(
  `${process.argv[2]}/${process.argv[3]}.ts`,
  process.argv[4] ? [...process.argv[4]] : []
)
