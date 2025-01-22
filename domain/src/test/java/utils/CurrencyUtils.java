package utils;

import com.currencyexchange.entity.Currency;
import com.currencyexchange.value.VCurrency;

public class CurrencyUtils {
  public static final Long ID = 1L;
  public static final Long ID2 = 2L;
  public static final Long ID3 = 3L;

  public static final String EUR_CODE = "EUR";
  public static final String USD_CODE = "USD";
  public static final String BGN_CODE = "BGN";


  public static VCurrency createCurrencyEUR(){
    VCurrency currency = new VCurrency();
    currency.setId(ID);
    currency.setCode(EUR_CODE);

    return currency;
  }
  public static VCurrency createCurrencyUSD(){
    VCurrency currency = new VCurrency();
    currency.setId(ID2);
    currency.setCode(USD_CODE);

    return currency;
  }
  public static VCurrency createCurrencyBGR(){
    VCurrency currency = new VCurrency();
    currency.setId(ID3);
    currency.setCode(BGN_CODE);

    return currency;
  }

  public static Currency createCurrencyEntityEUR() {
    return Currency.builder()
                   .id(ID)
                   .code(EUR_CODE)
                   .build();
  }

  public static Currency createCurrencyEntityUSD() {
    return Currency.builder()
                   .id(ID2)
                   .code(USD_CODE)
                   .build();
  }

  public static Currency createNewCurrencyEntityUSD(){
    return Currency.builder()
                   .code(USD_CODE)
                   .build();
  }

  public static Currency createCurrencyEntityBGR() {
    return Currency.builder()
                   .id(ID3)
                   .code(BGN_CODE)
                   .build();
  }

}
