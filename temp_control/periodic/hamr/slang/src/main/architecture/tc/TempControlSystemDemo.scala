package tc

object TempControlSystemDemo {

  def main(args: Array[String]): Unit = {
    art.Art.run(
      TempControlSystemArch.ad,
      TempControlSystemArch.services,
      art.scheduling.legacy.Legacy(TempControlSystemArch.ad.components)
    )
  }

}
