
#ifndef APPDEMO_US_H
#define APPDEMO_US_H

#include <string>

class Us {

public:
    char s;
    int age;

public:
    std::string toString() {
        std::string pi = "Us[s=" + std::to_string(s) +" : age="+ std::to_string(age)+ "]";
        return pi;
    }
};


#endif
