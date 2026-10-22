# Stock Watchlist & Analyzer

Java console app that reads daily NSE stock prices from a CSV file and lets you list stocks, view history, compare % change, rank gainers/losers, manage a watchlist, export a report, and see a 5-day moving average.

## Requirements

- JDK 8 or newer (`javac` and `java` on your PATH)
- No extra libraries

Check Java:

```powershell
java -version
javac -version
```

## How to run

### Option 1: `run.bat` (easiest on Windows)

1. Open `C:\Users\ina\Desktop\Abihaz`
2. Double-click `run.bat`
3. Wait for **Choose an option:**
4. Type a number and press Enter

The window stays open at the end so you can read the output.

### Option 2: PowerShell / Command Prompt

```powershell
cd C:\Users\ina\Desktop\Abihaz
javac -d out src\stockwatch\*.java
java -cp out stockwatch.Main
```

Use another CSV file:

```powershell
java -cp out stockwatch.Main path\to\other.csv
```

`prices.csv` should be in the project folder (the same folder you run from).

## Menu

| Option | What it does |
|--------|----------------|
| 1 | List all stocks in the file |
| 2 | Price history of one stock (by symbol) |
| 3 | % change from first close to last close |
| 4 | Top 3 gainers and top 3 losers |
| 5 | Highest and lowest closing price per stock |
| 6 | Watchlist: add, remove, view |
| 7 | Export top 3 gainers/losers to a CSV file |
| 8 | 5-day moving average (one symbol or `ALL`) |
| 0 | Exit |

Watchlist submenu: `a` add, `r` remove, `v` view, `b` back.

## Data file

`prices.csv` columns:

```text
date,symbol,open,high,low,close,volume
```

Sample data includes 6 NSE stocks with 10 trading days each:

- TCS, INFY, RELIANCE, HDFCBANK, ITC, SBIN

## Project layout

```text
Abihaz/
  prices.csv
  run.bat
  README.md
  src/stockwatch/
    Main.java
    PriceDataSource.java      (interface)
    CsvPriceLoader.java
    PriceRecord.java
    Stock.java
    Analyzer.java
    Watchlist.java
    ReportExporter.java
    MovingAveragePoint.java
    DataLoadException.java
    UnknownSymbolException.java
```

## Design

- **OOP:** `Stock`, `PriceRecord`, `Watchlist`, `Analyzer`, `Main`
- **Interface:** `PriceDataSource` (implemented by `CsvPriceLoader`)
- **I/O:** Java file I/O only (`Files`, `BufferedReader`, `BufferedWriter`)
- **Collections:** `List`, `Map`; ranking uses `Comparator`
- **Errors:** unknown symbol, empty file, malformed CSV row

## Sample results (from `prices.csv`)

Top 3 gainers: INFY, ITC, TCS  
Top 3 losers: RELIANCE, HDFCBANK, SBIN

Option 7 writes `gainers_losers.csv` by default (or any file name you type).
