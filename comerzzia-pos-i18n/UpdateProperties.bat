@echo off
FOR %%f IN (*.po) DO "msgcat" --properties-output %%f -o ..\comerzzia-pos-resources\src\main\resources\i18n\%%~nf.properties --no-location


"msgcat" --properties-output czz-pos.pot -o ..\comerzzia-pos-resources\src\main\resources\i18n\czz-pos.properties --no-location