**free
  ctl-opt main(main) dftactgrp(*no);

  //
  // Печатает массив на экран
  //
  dcl-proc printArrayElements;
    dcl-pi *n;
      array int(10) dim(1000) options(*varsize);
      arrayLength int(10) const;
    end-pi;

    dcl-s str varchar(52)
    dcl-s index int(10);

    str = 'Array elements: [';

    for index = 1 to arrayLength;
      str += %char(array(index)) + ' ';
    endfor;

    str += ']';

    dsply (str);
  end-proc;

  //
  // Сортировка массива методом пузырька
  //
  dcl-proc bubbleSort;
    dcl-pi *n;
      array int(10) dim(1000) options(*varsize);
      arrayLength int(10) const;
    end-pi;

    dcl-s i int(10);
    dcl-s j int(10);
    dcl-s temp int(10);

    for i = 1 to arrayLength - 1;
      for j = 1 to arrayLength - i;
        if array(j) > array(j + 1);
          temp = array(j);
          array(j) = array(j + 1);
          array(j + 1) = temp;
        endif;
      endfor;
    endfor;
  end-proc;

  dcl-proc main;
    dcl-pi *n;
    end-pi;

    dcl-pr randomNumber int(10) EXTPROC('rand');     
    end-pr;


    dcl-s array1 int(10) dim(*auto: 4);
    dcl-s array2 int(10) dim(*auto: 3);
    array1(1) = randomNumber();
    array1(2) = randomNumber();
    array1(3) = randomNumber();
    array1(4) = randomNumber();
    array2(1) = randomNumber();
    array2(2) = randomNumber();
    array2(3) = randomNumber();
        
    printArrayElements(array1: %elem(array1));
    bubbleSort(array1: %elem(array1));
    printArrayElements(array1: %elem(array1));

    printArrayElements(array2: %elem(array2));
    bubbleSort(array2: %elem(array2));
    printArrayElements(array2: %elem(array2));

    return;
  end-proc;

