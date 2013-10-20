#!/usr/bin/Rscript

library('e1071')

args <- commandArgs(trailingOnly = TRUE)
file <- args[1]
debut <- as.numeric(args[2])

data <- read.csv(file) 
taille <- length(data[,"Y"])
#debut <- taille-round(taille/6)
fin <- taille

data <-data
minX = min(data[,"X"])
maxX = max(data[,"X"])
data[,"X"] = (data[,"X"]-minX)/(maxX-minX)
#minY = min(data[,"Y"])
#maxY = max(data[,"Y"])
#data2[,"Y"] = (data[,"Y"]-minY)/(maxY-minY)

bordel = 5
donneesOrganisees <- matrix(0,fin-bordel+1,6)
for(alpha in bordel:fin){
  donneesOrganisees[(alpha-bordel+1),1:4] <- t(data[(alpha-bordel+1+1):(alpha),"Y"])
  donneesOrganisees[(alpha-bordel+1),5:6] <- t(data[(alpha-bordel+1):(alpha-3),"X"])
}
colnames(donneesOrganisees) <- c("Ylag3","Ylag2","Ylag1","Y","Xlag4","Xlag3")
trainingSet <- donneesOrganisees[1:debut,]
testingSet <- donneesOrganisees[-(1:debut),]

svm.model <- svm(Y ~ (.)^2, trainingSet, type='eps-regression')
svm.predi <- predict(svm.model,testingSet)
#prediction <- svm.predi*(maxY-minY) + minY

for(valeur in svm.predi){
  cat(valeur, labels = NULL)
  cat('\n')
}

#idefix <- debut+bordel-1
rval <- testingSet[,"Y"]
erreurRelative = sum((svm.predi - rval)/rval)/length(rval)
#cat('end\n')
#cat(erreurRelative,'\n')
