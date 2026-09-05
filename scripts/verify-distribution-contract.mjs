import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';

const contract = JSON.parse(
  readFileSync(new URL('../.zargard/distribution-contract.json', import.meta.url)),
);
const build = readFileSync(
  new URL('../app/build.gradle.kts', import.meta.url),
  'utf8',
);
const ui = readFileSync(
  new URL(
    '../app/src/main/java/com/pouriaquant/goldarb/ui/GoldArbApp.kt',
    import.meta.url,
  ),
  'utf8',
);

const fa = (value) =>
  String(value).replace(/[0-9]/g, (digit) => '۰۱۲۳۴۵۶۷۸۹'[Number(digit)]);

assert.match(
  build,
  new RegExp(`versionName = "${contract.distribution.androidVersion}"`),
);
assert.ok(ui.includes(`MetricCard("Platforms", "${fa(contract.coverage.catalogTotal)}"`));
assert.ok(ui.includes(`MetricCard("No Feed", "${fa(contract.coverage.missingFeeds)}"`));
assert.ok(ui.includes(`CoverageBucket("${fa(contract.coverage.configuredCollectors)}", "Public Collectors"`));
assert.ok(ui.includes(`ZarGard Android ${contract.distribution.androidVersion} Beta`));
assert.equal(contract.strategy.activePath, 'prefunded-cross-venue-inventory');
assert.equal(contract.strategy.requiresDirectBidAsk, true);
assert.equal(contract.strategy.requiresDirectionReversal, true);
assert.equal(contract.strategy.initialScreeningHours, 72);
assert.equal(contract.strategy.tokenizedGoldStatus, 'paused');
assert.ok(ui.includes('72h initial screening'));

console.log('Android distribution contract is consistent.');
