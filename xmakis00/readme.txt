Java 11. Potrebné stiahnuť JavaFX pomocou get-libs.sh

Demonštračná trieda: cz.fit.ijaproject.Main
Dokumentácia: ant doc

Demonštračná trieda sa skladá z parseru, ktorý spracováva data mapy zo súboru demoMap.
Z demoMap načíta ID a poziciu regálu, ktorý sa zobrazi vo výslednej mape. Data zo súboru
demoData reprezentujú popis toho čo sa nachádza v regáloch. Začína sa čítaním typov a 
kategorii jednotlivých polôžiek. Tieto načítané data sa priradia ešte v súbore
jednotlivým regálom. Potom čo sa tieto data sparsujú tak prebehne vytvorenie mapy
kde regále reprezentuju zelené obdĺžniky. Miesto pre doručenie tovaru, načítane z
demoMap je zobrazené ako červený ovál. žltá gulička reprezentuje štartovne miesto
vozíka. Na mape je možné si zobraziť kliknutím na regál jeho obsah a poprípade ho
upraviť. Kliknutím mimo regále je možné pridáť nový regál a zadať jeho obsah.
Táto posledná vec ešte nieje plne funkčná. Zatiaľ ani nefunguje možnosť vybrať si
súbor v prvej scéne, zaťial je iba načrtnutá.
