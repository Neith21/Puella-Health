from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from .models import PatientMedicalData
from .serializers import PatientMedicalDataSerializer

class PatientMedicalDataViewSet(viewsets.ModelViewSet):
    queryset = PatientMedicalData.objects.all()
    serializer_class = PatientMedicalDataSerializer
    permission_classes = [IsAuthenticated] # Obligatorio estar logueado

    # Al crear (POST)
    def perform_create(self, serializer):
        serializer.save(created_by=self.request.user)

    # Al editar (PUT/PATCH)
    def perform_update(self, serializer):
        serializer.save(modified_by=self.request.user)