from rest_framework import viewsets
from datetime import date
from .models import Patient
from .serializers import PatientSerializer
from rest_framework.permissions import IsAuthenticated

class PatientViewSet(viewsets.ModelViewSet):
    queryset = Patient.objects.all()
    serializer_class = PatientSerializer
    permission_classes = [IsAuthenticated] # Obligatorio estar logueado

    # Sobreescribimos este método para meter nuestra lógica custom
    def perform_create(self, serializer):
        user = self.request.user  # Obtenemos el usuario logueado
        
        # 1. Sacamos la fecha de nacimiento que viene en el request
        birth_date = serializer.validated_data.get('patient_birth_date')
        
        # 2. Calculamos la edad
        today = date.today()
        age = today.year - birth_date.year - (
            (today.month, today.day) < (birth_date.month, birth_date.day)
        )

        # 3. Guardamos inyectando los datos que faltan
        # Aquí rellenamos 'user', 'patient_age' y 'created_by' automáticamente
        serializer.save(
            user=user,
            created_by=user,
            patient_age=age
        )

    # Opcional: Si querés actualizar el 'modified_by' cuando editan
    def perform_update(self, serializer):
        serializer.save(modified_by=self.request.user)