package $package$.parent

import sttp.client4.SyncBackend
import yaga.wasmservice.client.WasmSttpBackend

object PlatformBackend:
  def sync: SyncBackend = new WasmSttpBackend()
