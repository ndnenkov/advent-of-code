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

const sums = _.zip(_.dropRight(depths, 2), depths.slice(1, -1), _.drop(depths, 2)).map(_.sum)
const increases = _.countBy(
  _.zip(_.dropRight(sums), _.drop(sums)),
  ([before, after]) => before! < after!
)['true']

console.log(increases)
