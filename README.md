# RIPv2 simulation protocol


Trumpai:
Kiekvienas routeris, kas 30 sec išsiunčia kaimynams savo routing lentelę. Po 180 sec, negavus kaimyninės lentelės, kelias iki jos interpretuojamas kaip nepasiekiamas.\
Kiekvienas routeris gavęs lentelę, patikrina eilutes, ar rastas trupesnis kelias arba pasižymi negaliojančius.\
Visi nepasiekiami keliai (metric = INFINITY  = 16), įtraukiami prie ištrinamų eilučių ir ištrinami praėjus\
garbage-collection (120) laikui.\
Simuliacijoje laikai trumpesni.


RIPv2Simulation.java - vykdo programos API\
Network.java - tinklo grafas, keičia tinklo struktūrą\
Router.java - routerio klasė. Broadcastina savo Routing Table.\
Host.java - nodes prijungti prie routeriu\
Link.java - edges tarp routeriu\
RoutingTableEntry.java - Routing Table lentelės eilutė, kurioje saugomi destination addr, subnet mask, next hop addr, metric, flag\
Packet.java - persiunčiamas iš vieno host į kitą. Atspausdina kelią.\
Packet2.java - routerių persiunčiamų žinučių struktūra
Main.java - paleidžiama programa

Kompliuojant visi *.class failai turi būt viename aplanke.

Paleidus programą, jau yra sukuriamas tinklas:
"->" - sujungti laidu
routeriai:
R1 -> R2,  R2 -> R5, R5 -> R6, R1 -> R3, R3 -> R4, R4 -> R5

hosts:
R1 -> H11, H12; R2 -> H21; R3 -> H31; R4 -> H41, H42, H43; R6 -> H61, H62, H63

Sukurtų routeriu ir hostu IP adresai:
R1 - "157.240.6.56",    R2 - "93.84.118.18",    R3 - "15.241.60.93",
R4 - "185.76.232.220",    R5 - "181.176.80.18",    R6 - "63.100.30.194"

H11 - "74.84.196.120",     H12 - "81.208.94.0",    H21 - "185.152.68.0"
H31 - "193.43.131.255",   H41 - "212.77.12.144",    H42 - "57.79.216.0",
H43 - "185.152.71.255",   H61 - "57.79.223.255",    H62 - "41.60.143.2"
H63 - "41.60.241.255"
 
TO FIX:
1. Host nesiunčia pranešimų, todėl panaikinus routerį, prie kurio jis yra prijungtas, Host Routing Table Entry visuose routeriuose išlieka, 
tačiau norint persiųst packet jis nebus pasiekiamas.
2. Routeriai dalinasi lentelėmis, bet ne response ir request žinutėmis(pradėta implementuoti packet2.java)
 
