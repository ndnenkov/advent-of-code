import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
target area: x=20..30, y=-10..-5
`

const [minX, maxX, minY, maxY] = [...readInput(sampleInput).trim().matchAll(/-?\d+/g)].map(
  coordinate => parseInt(coordinate[0])
)

function simulate(
  xVelocity: number,
  yVelocity: number,
  minX: number,
  maxX: number,
  minY: number,
  maxY: number
): number {
  let x = 0
  let y = 0
  let peakY = -1
  let landed = false

  while (x <= maxX && y >= minY) {
    peakY = _.max([peakY, y])!

    landed = landed || (x >= minX && y <= maxY)

    x += xVelocity
    y += yVelocity

    xVelocity -= xVelocity && (xVelocity > 0 ? 1 : -1)
    yVelocity -= 1
  }

  return landed ? peakY : -1
}

let max = 0
for (let x = 1; x <= maxX; x++) {
  for (let y = -Math.abs(minY); y <= Math.abs(minY); y++) {
    max = _.max([max, simulate(x, y, minX, maxX, minY, maxY)])!
  }
}

console.log(max)
