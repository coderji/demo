@echo off

::XT2423-6	SKU1 BR             BR
::XT2423-1	SKU2 LAS            LAE
::XT2423-2	SKU4 MEA/ANZ NFC    LAE
::XT2423-3	SKU5 EU NFC         EU
::XT2423-4	SKU6 ANZ CA         ANZ
::XT2423-5	SKU13 JP NFC        JP
::XT2425-1	SKU8 6000 LAS       LAE
::XT2425-2	SKU9 6000 IN        IN
::XT2425-3	SKU10 6000 MEA NFC  MEA
::XT2425-4	SKU11 6000 EU NFC   EU

set sku=%1
if "%sku%" == "" (
  for /f %%i in ('adb shell getprop ro.boot.hardware.sku') do (
    set sku=%%i
  )
)
echo SKU [%sku%]

if "%sku%" == "XT2423-6" (
  echo SKU1 BR             BR
) else if "%sku%" == "XT2423-1" (
  echo SKU2 LAS            LAE
) else if "%sku%" == "XT2423-2" (
  echo SKU4 MEA/ANZ NFC    LAE
) else if "%sku%" == "XT2423-3" (
  echo SKU5 EU NFC         EU
) else if "%sku%" == "XT2423-4" (
  echo SKU6 ANZ CA         ANZ
) else if "%sku%" == "XT2423-5" (
  echo SKU13 JP NFC        JP
) else if "%sku%" == "XT2425-1" (
  echo SKU8 6000 LAS       LAE
) else if "%sku%" == "XT2425-2" (
  echo SKU9 6000 IN        IN
) else if "%sku%" == "XT2425-3" (
  echo SKU10 6000 MEA NFC  MEA
) else if "%sku%" == "XT2425-4" (
  echo SKU11 6000 EU NFC   EU
) else (
  echo SKU not found
)