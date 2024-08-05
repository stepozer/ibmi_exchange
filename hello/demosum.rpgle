**free

  ctl-opt main(main) dftactgrp(*no);

  //
  // Вычисляет сумму квадратов (использует FOR)
  //
  dcl-proc sumOfNumbers;
    dcl-pi *n int(10);
      maxItems int(10) const;
    end-pi;

    dcl-s result int(10);
    dcl-s index int(10);

    result = 0;

    for index = 1 to maxItems;
      result += index * index;
    endfor;

    return result;
  end-proc;

  //
  // Вычисляет сумму квадратных корней (использует DO)
  //
  dcl-proc sumOfSrt;
    dcl-pi *n float(8);
      maxItems int(10) const;
    end-pi;

    dcl-s result float(8);
    dcl-s index int(10);

    result = 0;
    index = 1;

    dow (index < maxItems);
      result += %sqrt(%float(index));
      index = index + 1;
    enddo; 

    return result;
  end-proc;

  dcl-proc main;
    dcl-pi *n;
    end-pi;

    dsply ('Sum 50 = ' + %char(sumOfNumbers(50)));
    dsply ('Sum 10 = ' + %char(sumOfNumbers(10)));
    dsply ('SQRT 10 sum = ' + %char(sumOfSrt(10)));
    dsply ('SQRT 50 sum = ' + %char(sumOfSrt(50)));

    return;
  end-proc;
