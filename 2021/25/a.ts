import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
v...>>.vv>
.vv>>.vv..
>>.>v>...v
>>v>>.>.v.
v>v.vv.v..
>.>>..v...
.vv..>.>v.
v.v..>>v.v
....v..v.>
`

const seaFloor = readInput(sampleInput)
  .trim()
  .split('\n')
  .map(line => line.split('') as ('.' | '>' | 'v')[])

function move(
  seaFloor: ('.' | '>' | 'v')[][],
  direction: '>' | 'v'
): [('.' | '>' | 'v')[][], boolean] {
  const nextSeaFloor = _.cloneDeep(seaFloor)
  let moved = false

  seaFloor.forEach((line, y) => {
    line.forEach((cell, x) => {
      const nextX = direction === '>' ? (x + 1) % line.length : x
      const nextY = direction === '>' ? y : (y + 1) % seaFloor.length
      const nextCell = seaFloor[nextY][nextX]

      if (nextCell === '.' && cell === direction) {
        nextSeaFloor[nextY][nextX] = direction
        nextSeaFloor[y][x] = '.'
        moved = true
      }
    })
  })

  return [nextSeaFloor, moved]
}

function simulate(seaFloor: ('.' | '>' | 'v')[][], halfSteps = 0, moved = true): number {
  if (halfSteps % 2 === 1) {
    const [nextSeaFloor, verticalMove] = move(seaFloor, 'v')
    return simulate(nextSeaFloor, halfSteps + 1, moved || verticalMove)
  }

  if (moved) {
    const [nextSeaFloor, horizontalMove] = move(seaFloor, '>')
    return simulate(nextSeaFloor, halfSteps + 1, horizontalMove)
  }

  return halfSteps / 2
}

console.log(simulate(seaFloor))
