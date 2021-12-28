const fs = require('fs')

export function readInput(sampleInput: string): string {
  const taskNumber = process.argv[1].split('/').slice(-2)[0]
  const runSample = !!process.argv[2]

  return runSample
    ? sampleInput
    : fs.readFileSync(`inputs/${taskNumber}.txt`, {encoding: 'utf8', flag: 'r'})
}
