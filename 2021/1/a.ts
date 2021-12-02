import _ from 'lodash'

const input = `
199
200
208
210
200
207
240
269
260
263
`

const depths = input
  .trim()
  .split('\n')
  .map(depth => +depth)

const increases = _.countBy(
  _.zip(_.dropRight(depths), _.drop(depths)),
  ([before, after]) => before! < after!
)['true']

console.log(increases)
