import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2
`

const lines = readInput(sampleInput)
  .trim()
  .split('\n')
  .map(line =>
    line.split(' -> ').map(point => point.split(',').map(coordinate => parseInt(coordinate)))
  )

const visits: {[key: string]: number} = {}

lines.forEach(([[x1, y1], [x2, y2]]) => {
  const xStep = Math.sign(x2 - x1)
  const yStep = Math.sign(y2 - y1)

  let x = x1
  let y = y1
  while (true) {
    const key = `${x}:${y}`
    visits[key] ??= 0
    visits[key] += 1

    if (x === x2 && y === y2) break

    x += xStep
    y += yStep
  }
})

const dangerousAreas = _.values(visits).filter(areaVisits => areaVisits > 1).length
console.log(dangerousAreas)
