import _ from 'lodash'
import {readInput} from '../readInput'

const sampleInput = `
..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

#..#.
#....
##..#
..#..
..###
`

const algorithm = readInput(sampleInput).trim().split('\n\n')[0].split('') as ('.' | '#')[]
const image = readInput(sampleInput).trim().split('\n\n')[1]

function padImage(image: string) {
  const lineLength = image.split('\n')[0].length
  const oneLinePadding = '.'.repeat(lineLength + 100)
  const verticalPadding = `${oneLinePadding}\n`.repeat(100)

  const paddedLines = image
    .split('\n')
    .map(line => `${'.'.repeat(50)}${line}${'.'.repeat(50)}`)
    .join('\n')

  return `${verticalPadding}${paddedLines}\n${verticalPadding}`
}

function enhanced(
  pixelatedImage: ('.' | '#')[][],
  x: number,
  y: number,
  algorithm: ('.' | '#')[],
  blank: '.' | '#'
): '.' | '#' {
  const section = [
    pixelatedImage[y - 1]?.[x - 1],
    pixelatedImage[y - 1]?.[x],
    pixelatedImage[y - 1]?.[x + 1],
    pixelatedImage[y][x - 1],
    pixelatedImage[y][x],
    pixelatedImage[y][x + 1],
    pixelatedImage[y + 1]?.[x - 1],
    pixelatedImage[y + 1]?.[x],
    pixelatedImage[y + 1]?.[x + 1]
  ].map(pixel => pixel ?? blank)

  return algorithm[parseInt(section.join('').replace(/\./g, '0').replace(/#/g, '1'), 2)]
}

function enhance(pixelatedImage: ('.' | '#')[][], algorithm: ('.' | '#')[], blank: '.' | '#') {
  return pixelatedImage.map((line, y) =>
    line.map((_pixel, x) => enhanced(pixelatedImage, x, y, algorithm, blank))
  )
}

function enhance50times(pixelatedImage: ('.' | '#')[][], algorithm: ('.' | '#')[]) {
  const swap = _.first(algorithm) === '#' && _.last(algorithm) === '.'
  let image = pixelatedImage

  for (let run = 1; run <= 50; run++) {
    image = enhance(image, algorithm, swap && run % 2 === 0 ? '#' : '.')
  }

  return image
}

const pixelatedImage = padImage(image)
  .split('\n')
  .map(line => line.split('') as ('.' | '#')[])

const enhancedImage = enhance50times(pixelatedImage, algorithm)
const count = _.flatten(enhancedImage).filter(pixel => pixel === '#').length
console.log(count)
