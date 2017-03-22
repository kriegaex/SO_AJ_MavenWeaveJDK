package de.scrum_master.aspect;

public aspect StringExtenderAspect {
  public String String.repeat(int repetitions) {
    StringBuilder repeatedText = new StringBuilder(length() * repetitions);
    for (int i = 0; i < repetitions; i++)
      repeatedText.append(this);
    return repeatedText.toString();
  }
}
