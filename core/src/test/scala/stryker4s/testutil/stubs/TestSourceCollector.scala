package stryker4s.testutil.stubs

import java.nio.file.Path

import stryker4s.mutants.findmutants.SourceCollector
import stryker4s.run.process.ProcessRunner

class TestSourceCollector(returns: Iterable[Path]) extends SourceCollector {
  override def collectFilesToMutate(): Iterable[Path] = returns
  override def filesToCopy: Iterable[Path] = returns
  override protected lazy val processRunner: ProcessRunner = ProcessRunner()
}
