#!/usr/bin/Rscript
library(xts)
library(forecast)
library(e1071)

args <- commandArgs(trailingOnly = TRUE)
file <- args[1]
putain <- as.numeric(args[2])

data <- read.csv(file) 
taille <- dim(data)[1]
trainingSet <- matrix(data=NA, nrow=0, ncol=38)
colnames(trainingSet) <- colnames(trainingSet, do.NULL = FALSE, prefix = "C")
colnames(trainingSet)[37] <- 'predicion'
colnames(trainingSet)[38] <- 'trueVal'

# fabrique de quoi apprendre le correcteur (SVM)
for(alpha in (putain-round(putain/5)) : putain){
  tryCatch({ 
    # cpu
    alldata <- data$CPUBUSY[1:alpha]
    debut <- length(alldata)%%24
    cputraindata <- alldata[debut:(length(alldata)-48)]
    cpuserie <- ts(cputraindata, frequency=24)
    logcpuserie <- log(cpuserie)
    logcpuforecast <- HoltWinters(logcpuserie)
    logcpuforecast2 <- forecast.HoltWinters(logcpuforecast, h=48)
    cpuforecast <- exp(logcpuforecast2$mean)
    cpuforecast <- as.numeric(cpuforecast)
    
    # waitio
    alldata <- data$WAITIO[1:alpha]
    iotraindata <- alldata[debut:(length(alldata)-48)]
    ioserie <- ts(iotraindata, frequency=24)
    logioserie <- log(ioserie)
    logioforecast <- HoltWinters(logioserie)
    logioforecast2 <- forecast.HoltWinters(logioforecast, h=48)
    ioforecast <- exp(logioforecast2$mean)
    ioforecast <- as.numeric(ioforecast)
    
    # Je rajoute les predictions aux données historiques
    cpuintermediaire <- c(cputraindata, cpuforecast)
    iointermediaire <- c(iotraindata, ioforecast)
    
    l <- length(cputraindata)
    multi <- matrix(data = NA, nrow = 48, ncol = 38)
    colnames(multi) <- colnames(multi, do.NULL = FALSE, prefix = "C")
    for(i in 1:48){
      debut <- l + i - 35
      fin <- l + i
      multi[i,1:36] <- iointermediaire[debut:fin]
      multi[i,37] <- cpuintermediaire[l+i]
      multi[i,38] <- data$CPUBUSY[alpha-48+i]  
    }
    trainingSet = rbind(trainingSet,multi)
  },error = function(e) print('une prédiction à planté\n') )
}
cat('dimension des données pour apprendre',dim(trainingSet))
svm.model <- svm(trueVal ~ . , data=trainingSet, cost=0.1)
print(svm.model)

result <- matrix(data=NA, nrow=0, ncol=1)

for(i in 1:ceiling((length(data$CPUBUSY)-putain)/48)){
  alpha <- putain + i*48
  cat(c(i,"eme prediction de 48h : de ", alpha," à ", alpha+48, "\n"))
    # cpu
    alldata <- data$CPUBUSY[1:alpha]
    debut <- length(alldata)%%24
    cputraindata <- alldata[debut:(length(alldata)-48)]
    cpuserie <- ts(cputraindata, frequency=24)
    logcpuserie <- log(cpuserie)
    logcpuforecast <- HoltWinters(logcpuserie)
    logcpuforecast2 <- forecast.HoltWinters(logcpuforecast, h=48)
    cpuforecast <- exp(logcpuforecast2$mean)
    cpuforecast <- as.numeric(cpuforecast)
    
    # waitio
    alldata <- data$WAITIO[1:alpha]
    iotraindata <- alldata[debut:(length(alldata)-48)]
    ioserie <- ts(iotraindata, frequency=24)
    logioserie <- log(ioserie)
    logioforecast <- HoltWinters(logioserie)
    logioforecast2 <- forecast.HoltWinters(logioforecast, h=48)
    ioforecast <- exp(logioforecast2$mean)
    ioforecast <- as.numeric(ioforecast)
    
    # Je rajoute les predictions aux données historiques
    cpuintermediaire <- c(cputraindata, cpuforecast)
    iointermediaire <- c(iotraindata, ioforecast)
    
    l <- length(cputraindata)
    multi <- matrix(data = 0, nrow = 48, ncol = 38)
    colnames(multi) <- colnames(trainingSet)
    for(i in 1:48){
      debut <- l + i - 35
      fin <- l + i
      multi[i,1:36] <- iointermediaire[debut:fin]
      multi[i,37] <- cpuintermediaire[l+i]
    }
    svm.pred <- predict(svm.model, multi)
    result <- rbind(result,t(t(svm.pred)))
}


write.csv(result, file=paste(file,".prediction",sep=""), row.names=FALSE)
