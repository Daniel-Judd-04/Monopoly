**Monopoly**

This project is an (almost) complete Java representation of the game Monopoly.
Features:
- A graphically displayed board.
- Buying, Selling and Trading
- House and hotel development

Location Context Key:
- "-" = Unowned
- "X" = Mortgaged
- "H" = Hotel
- "?" = Chance
- "C" = Community Chest
- "[1-4]" = Number of houses

**Example Board (B&W):**
```
 =-=-=-=-=-=-=-= Current Board =-=-=-=-=-=-=-=  |  Player Information:
 Remaining Houses = 32 | Remaining Hotels = 12  |  Bob (£1500) At: Pentonville Rd.
                                                |  Doesn't own any properties
                                                |  Will (£1500) At: King's Cross Stn.
 | P |█-█|█?█|█-█|█-█|█-█|█-█|█-█|█-█|█-█|GTJ|  |  Doesn't own any properties
 |█-█|                                   |█-█|  |  John (£1500) At: Pentonville Rd.
 |█-█|                                   |█-█|  |  Doesn't own any properties
 |█C█|                                   |█C█|  |
 |█-█|                                   |█-█|  |
 |█-█|             MONOPOLY!             |█-█|  |
 |█-█|                                   |█?█|  |
 |█-█|                                   |█-█|  |
 |█-█|                                   |█T█|  |
 |█-█|                                   |█-█|  |
 | J |█-█|█-█|█?█|█-█|█-█|█T█|█-█|█C█|█-█|GO!|  |
       ^               ^                        |
      (Bob/John)      (Will)                    |
 =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=  |
John (£1500) is currently at Pentonville Road [-] (£120)
```
