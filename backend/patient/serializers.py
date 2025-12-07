from rest_framework import serializers
from .models import Patient

class PatientSerializer(serializers.ModelSerializer):
    class Meta:
        model = Patient
        fields = '__all__'
        # ESTA ES LA CLAVE:
        # Estos campos NO se piden en el POST, el backend los rellena
        read_only_fields = (
            'patient_age', 
            'user', 
            'created_by', 
            'modified_by', 
            'created_at', 
            'updated_at',
            'active'
        )