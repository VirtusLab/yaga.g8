package $package$.parent

import sttp.client4.SyncBackend
import sttp.client4.httpclient.HttpClientSyncBackend

object PlatformBackend:
  def sync: SyncBackend = HttpClientSyncBackend()
