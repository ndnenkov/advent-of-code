import _ from 'lodash'

const input = `
target area: x=20..30, y=-10..-5
`

const [minX, maxX, minY, maxY] = [...input.trim().matchAll(/-?\d+/g)].map(x => parseInt(x[0]))

function lands(
  xVelocity: number,
  yVelocity: number,
  minX: number,
  maxX: number,
  minY: number,
  maxY: number
): boolean {
  let x = 0
  let y = 0

  while (x <= maxX && y >= minY) {
    if (x >= minX && y <= maxY) return true

    x += xVelocity
    y += yVelocity

    xVelocity -= xVelocity && (xVelocity > 0 ? 1 : -1)
    yVelocity -= 1
  }

  return false
}

let count = 0
for (let x = 1; x <= maxX; x++) {
  for (let y = -Math.abs(minY); y <= Math.abs(minY); y++) {
    if (lands(x, y, minX, maxX, minY, maxY)) count += 1
  }
}

console.log(count)
